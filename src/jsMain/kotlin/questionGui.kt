import io.ktor.http.*
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.*
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLInputElement
import org.w3c.xhr.XMLHttpRequest
import react.*
import react.dom.*


external interface QuestionProps : RProps {
    //var question: QuestionData?
}

data class QuestionState(
    var question: QuestionData,
    var answer: String,
    var result: String
) : RState


@JsExport
class QuestionComponent(props: QuestionProps) : RComponent<QuestionProps, QuestionState>(props) {

    override fun RBuilder.render() {
        button {
            attrs {
                type = ButtonType.button
                onClickFunction = {
                    val src = js("getCode()") as String
                    val params = listOf(
                        "code" to src //document.getElementById("code")?.textContent,
                    ).formUrlEncode()
                    val url = "ask?$params"
                    val t = getAsync(url) {
                        val q = JSON.parse<QuestionData>(it)
                        setState {
                            question = q
                        }
                    }
                }
            }
            +"Ask me something"
        }
        div {
            attrs {
                visible = state.question != null
            }
            p {
                +state.question?.text
            }
           // if(state.question?.type == QuestionType.Id.name) {
                input {
                    attrs {
                        type = InputType.text
                        //id = "answer"
                        onChangeFunction = {
                            val target = it.target as HTMLInputElement
                            setState {
                                answer = target.value
                            }
                        }
                    }
                }
           // }

            br {}

            button {
                attrs {
                    type = ButtonType.button
                    onClickFunction = {
                        sendAnswer()
                    }
                }
                +"Send"
            }
            p {
//            +when(state.result.name){
//                Result.Error -> "!!"
//                Result.Correct -> "Correct"
//                Result.Incorrect -> "Incorrect"
//            }
                +state.result
            }
        }
    }

    private fun sendAnswer() {
        val params = listOf(
            "qid" to state.question.id,
            "answer" to state.answer,
            "code" to js("getCode()") as String
        ).formUrlEncode()
        val url = "answer?$params"
        val t = getAsync(url) {
            val a = JSON.parse<QuestionAnswer>(it)
            setState {
                result = a.result
            }
            if (a.result == "Error")
                window.alert("invalid request")
        }
    }

}



private fun getAsync(url: String, callback: (String) -> Unit) {
    val xmlHttp = XMLHttpRequest()
    xmlHttp.open("GET", url)
    xmlHttp.onload = {
        if (xmlHttp.readyState == 4.toShort() && xmlHttp.status == 200.toShort()) {
            callback.invoke(xmlHttp.responseText)
        }
    }
    xmlHttp.send()
}