package net.wetheGoverned.data

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.request.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import net.wetheGoverned.repository.*
import net.wetheGoverned.model.*
import net.wetheGoverned.util.AddressUtils

/**
 * Shared logic for the "Extensive Web Dashboard"
 * Recreates ALL Android App features for the web browser.
 */
fun Route.setupWebDashboard(
    pollRepository: PollRepository,
    manifestoRepository: ManifestoRepository,
    residentRepository: ResidentRepository,
    voteRepository: VoteRepository,
    accountRepository: AccountRepository
) {
    val sharedStyles = """
        :root {
            --bg: #0f172a;
            --card-bg: #1e293b;
            --primary: #3b82f6;
            --accent: #22c55e;
            --warning: #f59e0b;
            --danger: #ef4444;
            --text: #f8fafc;
            --text-dim: #94a3b8;
            --border: #334155;
        }
        body { font-family: 'Inter', system-ui, sans-serif; background: var(--bg); color: var(--text); margin: 0; line-height: 1.5; }
        .header { background: var(--card-bg); padding: 1rem 2rem; border-bottom: 2px solid var(--primary); display: flex; justify-content: space-between; align-items: center; position: sticky; top: 0; z-index: 100; }
        nav a { color: var(--text-dim); text-decoration: none; margin-left: 1.5rem; font-weight: 500; font-size: 0.95rem; }
        nav a:hover, nav a.active { color: var(--primary); }
        .container { max-width: 1200px; margin: 0 auto; padding: 2rem; }
        .grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 1.5rem; }
        .card { background: var(--card-bg); padding: 1.5rem; border-radius: 12px; border: 1px solid var(--border); margin-bottom: 1rem; }
        .btn { padding: 0.75rem 1.5rem; border-radius: 10px; border: none; font-weight: 600; cursor: pointer; transition: all 0.2s; text-decoration: none; display: inline-block; text-align: center; }
        .btn-primary { background: var(--primary); color: white; width: 100%; border: none; }
        .btn-outline { background: transparent; border: 1px solid var(--border); color: var(--text); }
        input, textarea { background: #0f172a; border: 1px solid var(--border); color: white; padding: 0.85rem; border-radius: 10px; width: 100%; box-sizing: border-box; margin-top: 0.5rem; margin-bottom: 1rem; }
        label { font-size: 0.9rem; color: var(--text-dim); }
        .error-msg { color: var(--danger); background: rgba(239, 68, 68, 0.1); padding: 10px; border-radius: 8px; margin-bottom: 1rem; text-align: center; }
        .badge { padding: 4px 8px; border-radius: 6px; font-size: 0.75rem; font-weight: bold; text-transform: uppercase; }
        .badge-verified { background: rgba(34, 197, 94, 0.2); color: var(--accent); }
        .badge-observer { background: rgba(245, 158, 11, 0.2); color: var(--warning); }
    """.trimIndent()

    fun template(title: String, activeTab: String, content: String, showNav: Boolean = true): String {
        return """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>$title | WeTheGoverned</title>
            <style>$sharedStyles</style>
        </head>
        <body>
            ${if (showNav) """
            <header class="header">
                <div style="display:flex; align-items:center; gap:10px">
                    <span style="font-size:1.5rem">🗳️</span>
                    <h2 style="margin:0">WeTheGoverned</h2>
                </div>
                <nav>
                    <a href="/" class="${if(activeTab=="home") "active" else ""}">Polls</a>
                    <a href="/profile" class="${if(activeTab=="profile") "active" else ""}">Profile</a>
                    <a href="/api/logout" style="color:var(--danger)">Logout</a>
                </nav>
            </header>
            """ else ""}
            <div class="container">$content</div>
        </body>
        </html>
    """.trimIndent()
    }

    fun isAuthorized(call: ApplicationCall): Boolean = call.request.cookies["auth"] == "true"

    get("/login") {
        val error = call.request.queryParameters["error"]
        val content = """
            <div style="max-width:400px; margin:80px auto;">
                <h1 style="text-align:center;">Login to Node</h1>
                <div class="card">
                    ${if (error != null) "<div class='error-msg'>$error</div>" else ""}
                    <form action="/api/login" method="post">
                        <label>Username</label><input type="text" name="username" required>
                        <label>Password</label><input type="password" name="password" required>
                        <button type="submit" class="btn btn-primary">Login</button>
                    </form>
                    <p style="text-align:center; margin-top:1.5rem; font-size:0.9rem;">
                        New node? <a href="/register" style="color:var(--primary)">Initialize Identity</a>
                    </p>
                </div>
            </div>
        """.trimIndent()
        call.respondText(template("Login", "login", content, false), ContentType.Text.Html)
    }

    get("/register") {
        val error = call.request.queryParameters["error"]
        val content = """
            <div style="max-width:400px; margin:80px auto;">
                <h1 style="text-align:center;">Initialize Identity</h1>
                <div class="card">
                    ${if (error != null) "<div class='error-msg'>$error</div>" else ""}
                    <form action="/api/register" method="post">
                        <label>Username</label><input type="text" name="username" required>
                        <label>Password</label><input type="password" name="password" required>
                        <button type="submit" class="btn btn-primary">Create Account</button>
                    </form>
                    <p style="text-align:center; margin-top:1.5rem; font-size:0.9rem;">
                        Already have an account? <a href="/login" style="color:var(--primary)">Login here</a>
                    </p>
                </div>
            </div>
        """.trimIndent()
        call.respondText(template("Register", "register", content, false), ContentType.Text.Html)
    }

    post("/api/login") {
        val params = call.receiveParameters()
        val username = params["username"] ?: ""
        val password = params["password"] ?: ""
        
        accountRepository.login(username, password).fold(
            onSuccess = { account ->
                call.response.cookies.append("auth", "true", path = "/")
                call.response.cookies.append("user", account.pubKey, path = "/")
                call.respondRedirect("/")
            },
            onFailure = {
                call.respondRedirect("/login?error=Invalid credentials")
            }
        )
    }

    post("/api/register") {
        val params = call.receiveParameters()
        val username = params["username"] ?: ""
        val password = params["password"] ?: ""
        
        val keyPair = net.wetheGoverned.core.Secp256k1KeyManager.generateKeyPair()
        val newAccount = UserAccount(username, password, keyPair.pubKeyHex, keyPair.privateKeyHex)

        accountRepository.register(newAccount).fold(
            onSuccess = {
                call.response.cookies.append("auth", "true", path = "/")
                call.response.cookies.append("user", newAccount.pubKey, path = "/")
                call.respondRedirect("/")
            },
            onFailure = {
                call.respondRedirect("/register?error=${it.message}")
            }
        )
    }

    get("/api/logout") {
        call.response.cookies.append("auth", "false", path = "/", expires = io.ktor.util.date.GMTDate.START)
        call.respondRedirect("/login")
    }

    get("/") {
        if (!isAuthorized(call)) {
            call.respondRedirect("/login")
            return@get
        }
        
        val user = call.request.cookies["user"] ?: "User"
        val polls = withTimeoutOrNull(500) { pollRepository.observeDistrictPolls("FL-06").first() } ?: emptyList()
        
        val content = """
            <div style="margin-bottom: 2rem;">
                <h1>Welcome, $user</h1>
                <p style="color: var(--text-dim);">You are connected to the FL-06 District Mesh.</p>
            </div>

            <div class="card" style="border-left: 4px solid var(--accent);">
                <h3 style="margin-top:0">Node Status</h3>
                <p style="margin-bottom:0">✅ Serving on Port 80 | Domain: wethegoverned.usa</p>
            </div>

            <h2 style="margin-top: 2rem;">District Polls</h2>
            <div class="grid">
                ${if (polls.isEmpty()) """
                    <div class="card">
                        <p style="color: var(--text-dim); text-align: center;">No polls synced yet. Connect to peers to receive data.</p>
                    </div>
                """ else polls.joinToString("") { poll -> """
                    <div class="card">
                        <h3 style="margin-top:0">${poll.question}</h3>
                        <p style="font-size: 0.9rem; color: var(--text-dim);">District: ${poll.districtId}</p>
                        <div style="display: flex; justify-content: space-between; align-items: center; margin-top: 1rem;">
                            <span>${poll.totalVotes} votes</span>
                            <a href="/poll/${poll.id}" class="btn btn-outline" style="padding: 0.5rem 1rem;">Vote</a>
                        </div>
                    </div>
                """ }}
            </div>
        """.trimIndent()
        call.respondText(template("Dashboard", "home", content), ContentType.Text.Html)
    }

    get("/profile") {
        if (!isAuthorized(call)) {
            call.respondRedirect("/login")
            return@get
        }
        val user = call.request.cookies["user"] ?: "User"
        
        val content = """
            <h1>My Node Profile</h1>
            <div class="card">
                <h3>Identity: $user</h3>
                <p>Status: <span class="badge badge-observer">Observer - Basic Access</span></p>
                <hr style="border:0; border-top:1px solid var(--border); margin:1.5rem 0;">
                <h4>Get Verified</h4>
                <p style="font-size:0.9rem; color:var(--text-dim);">Verify your district address to participate in official polls.</p>
                <form action="/api/verify" method="post">
                    <label>Street Address</label><input type="text" name="address" required>
                    <div style="display:grid; grid-template-columns: 1fr 1fr; gap:10px;">
                        <div><label>City</label><input type="text" name="city" required></div>
                        <div><label>Zip Code</label><input type="text" name="zip" required></div>
                    </div>
                    <button type="submit" class="btn btn-primary">Submit Verification</button>
                </form>
            </div>
        """.trimIndent()
        call.respondText(template("Profile", "profile", content), ContentType.Text.Html)
    }

    post("/api/verify") {
        if (!isAuthorized(call)) {
            call.respond(HttpStatusCode.Unauthorized)
            return@post
        }
        val p = call.receiveParameters()
        val street = p["address"] ?: ""
        val city = p["city"] ?: ""
        val zip = p["zip"] ?: ""
        
        val fingerprint = AddressUtils.generateFingerprint(street, city, zip)
        val user = call.request.cookies["user"] ?: "admin"
        
        residentRepository.upgradeTierFull(user, VerificationTier.VERIFIED, fingerprint)
        call.respondRedirect("/profile")
    }
}
