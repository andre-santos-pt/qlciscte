package pt.iscte.qlc.server

import QuestionAnswer
import io.ktor.application.*
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.features.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.html.*
import pt.iscte.paddle.model.*
import pt.iscte.paddle.model.javaparser.*
import io.ktor.gson.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URL


fun HTML.index() {

    head {
        title("Hello from Ktor!")
        script(src = "/static/codemirror/codemirror.js") {}
        link(rel = "stylesheet", href = "/static/codemirror/codemirror.css") {}
        link(rel = "stylesheet", href = "/static/codemirror/eclipse.css") {}
        script(src = "/static/codemirror/docs.js") {}
        script(src = "/static/codemirror/clike.js") {}
    }
    body {
        img {
            src = "/static/iscte-logo.jpg"
            width = "100"
        }
        button {
            GlobalScope.launch {
                getC("test")
            }
            +"test"
        }
        script(src = "/static/editor.js") {}

        div {
            id = "question"
        }
        script(src = "/static/output.js") {}
    }
}

suspend fun getC(code: String) {
    val client = HttpClient()


    val content: String = client.post("https://script.google.com/macros/s/AKfycbxfW168PaSe0ahyvQ_z5lEWiXeMxNE1sqwK_WMN3qhfPHCbWlacC1aP/exec") {
        header("code", code)
    }

    println(content)
}

fun Application.main() {
    install(DefaultHeaders)
    install(CallLogging)
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }
    install(Routing) {

        get("/") {
            call.respondHtml(HttpStatusCode.OK, HTML::index)
            //call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }
        get("/ask") {
            val code = call.request.queryParameters["code"]
            println(code)
            val parser = Java2Paddle("test", code)
            val model = parser.parse()
            //println(model)
            val list = qSetGen(model)
            val q = list.random()
            call.respond(q)
        }
        get("/answer") {
            val qid = call.request.queryParameters["qid"]
            val answer = call.request.queryParameters["answer"]
            val code = call.request.queryParameters["code"]
            val parser = Java2Paddle("test", code)
            val model = parser.parse()
            val q = set.find { it.qid() == qid }
            println("qid: $q")
            if (q == null)
                call.respond(QuestionAnswer("N/A"))
            else {
                println(answer)

                val ans = q.answer(model.procedures[0])
                println("ans: $ans")
                val correct = if (answer == ans) "Correct" else "Incorrect"
                getC(code!!)
                call.respond(QuestionAnswer(correct, ans))
            }
        }

        static("/static") {
            resources()
            resources("images")
            resources("codemirror")
        }
    }
}

//fun main(args: Array<String>) {
//    embeddedServer(Netty, 8080, watchPaths = listOf("BlogAppKt"), module = Application::module).start()
//}


//fun main() {
//    embeddedServer(Netty, java.lang.System.getenv("PORT")?.toInt() ?: 8080) {
//        install(ContentNegotiation) {
//            gson {
//                setPrettyPrinting()
//            }
//        }
//
//        routing {
//            get("/") {
//                //call.respondHtml(HttpStatusCode.OK, HTML::index)
//                call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
//            }
//            get("/ask") {
//                val code = call.request.queryParameters["code"]
//                println(code)
//                val parser = Java2Paddle("test", code)
//                val model = parser.parse()
//                //println(model)
//                val list = qSetGen(model)
//                val q = list.random()
//                call.respond(q)
//            }
//            get("/answer") {
//                val qid = call.request.queryParameters["qid"]
//                val answer = call.request.queryParameters["answer"]
//                val code = call.request.queryParameters["code"]
//                val parser = Java2Paddle("test", code)
//                val model = parser.parse()
//                val q = set.find { it.qid() == qid }
//                if(q == null)
//                    call.respond(QuestionAnswer("N/A"))
//                else {
//                    println(answer)
//                    val ans = q.answer(model.procedures[0])
//                    val correct = if(answer == ans) "Correct" else "Incorrect"
//                    call.respond(QuestionAnswer(correct, ans))
//                }
//            }
//
//            static("/static") {
//                resources()
//                resources("images")
//                resources("codemirror")
//            }
//        }
//    }.start(wait = true)
//}


fun FlowContent.paramsQuestion(model: IModule) {

    div(classes = "w3-panel") {
        h1 {
            +"How many parameters?"
        }

        form("/answer", method = FormMethod.get, encType = FormEncType.multipartFormData) {
            textInput(name = "answer")
            input {
                type = InputType.submit
                value = "Send"
            }
        }
    }


}

fun FlowContent.questionA(code: String) {
    div(classes = "w3-panel") {
        +"How many variables?"
        +"Code: "
        +code
        textInput {

        }
        button {

        }
    }
}

fun FlowContent.printCode(code: String) {
    div(classes = "w3-panel") {
        +code
    }
}