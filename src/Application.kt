package cberry.dev

import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.html.respondHtml
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.routing
import kotlinx.css.CSSBuilder
import kotlinx.css.Color
import kotlinx.css.em
import kotlinx.css.p
import kotlinx.html.CommonAttributeGroupFacade
import kotlinx.html.FlowOrMetaDataContent
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.li
import kotlinx.html.style
import kotlinx.html.ul

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    routing {
        helloWorld()
        htmlDsl()
        css()
        facts()
    }
}


fun Route.helloWorld() {
    get("/") {
        call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
    }
}

fun Route.htmlDsl() {
    get("/html-dsl") {
        call.respondHtml {
            body {
                h1 { +"HTML" }
                ul {
                    for (n in 1..10) {
                        li { +"$n" }
                    }
                }
            }
        }
    }
}

fun Route.facts() {
    get("/facts") {

        val client = HttpClient(OkHttp) {
            engine {
                config {
                    followRedirects(true)
                }
            }
        }

        val params = context.request.queryParameters
        val month = params["month"].orEmpty()
        val day = params["day"].orEmpty()

        call.respondText {
            client.get("https://numbersapi.p.rapidapi.com/$month/$day/date") {
                headers {
                    append("x-rapidapi-host", "numbersapi.p.rapidapi.com")
                    append("x-rapidapi-key", "53b2845fafmshe2ad5ac0040c20dp170b5cjsn721604d25b4f")
                }
            }
        }
    }
}

fun Route.css() {
    get("/styles.css") {
        call.respondCss {
            kotlinx.css.body {
                backgroundColor = Color.red
            }
            p {
                fontSize = 2.em
            }
            rule("p.myclass") {
                color = Color.blue
            }
        }
    }
}

fun FlowOrMetaDataContent.styleCss(builder: CSSBuilder.() -> Unit) {
    style(type = ContentType.Text.CSS.toString()) {
        +CSSBuilder().apply(builder).toString()
    }
}

fun CommonAttributeGroupFacade.style(builder: CSSBuilder.() -> Unit) {
    this.style = CSSBuilder().apply(builder).toString().trim()
}

suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit) {
    this.respondText(CSSBuilder().apply(builder).toString(), ContentType.Text.CSS)
}
