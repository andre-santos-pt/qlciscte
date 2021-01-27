package pt.iscte.qlc.server

import QuestionData
import QuestionType
import pt.iscte.paddle.model.IModule
import pt.iscte.paddle.model.IProcedure

val set = listOf(
    QName(),
    QNParams(),
    QReturn(),
    QRecursive()
)

fun qSetGen(m: IModule) : List<QuestionData> {
    val questions = mutableListOf<QuestionData>()
    m.procedures.forEach{p -> set.filter {it.appliesTo(p)}.forEach { questions.add(it.question(p)) }}
   return questions
}

interface QGen {
    fun qid() = javaClass.simpleName
    fun appliesTo(proc: IProcedure) : Boolean = true
    fun question(proc: IProcedure) : QuestionData
    fun answer(proc: IProcedure) : String
}

class QName : QGen {
    override fun question(proc: IProcedure) =
        QuestionData(qid(), QuestionType.Id.name, "What is the name of this function?")
    override fun answer(proc: IProcedure) = proc.id
}

class QNParams : QGen {
    override fun question(proc: IProcedure) =
        QuestionData(qid(), QuestionType.Integer.name, "How many parameters does function F has?")
    override fun answer(proc: IProcedure) = if(proc.`is`("INSTANCE")) (proc.parameters.size-1).toString()
                                    else proc.parameters.size.toString()
}

class QReturn : QGen {
    override fun appliesTo(proc: IProcedure) = !proc.returnType.isVoid
    override fun question(proc: IProcedure) =
        QuestionData(qid(), QuestionType.Id.name, "What is the return type of function <i>${proc.id}</i>?")
    override fun answer(proc: IProcedure) = proc.returnType.id
}

class QRecursive : QGen {
    override fun question(proc: IProcedure) =
        QuestionData(qid(), QuestionType.Bool.name, "Is function <b>${proc.id}</b> recursive?")
    override fun answer(proc: IProcedure) = proc.isRecursive.toString()
}

