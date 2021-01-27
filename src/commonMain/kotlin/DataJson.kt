

enum class QuestionType {
    Integer,
    Id,
    Bool,
    MultipleChoice,
    SingleChoice
}

enum class Result {
    Error,
    Correct,
    Incorrect
}

data class QuestionData(val id: String, val type: String, val text: String, val options: List<String> = listOf())

data class QuestionAnswer(val result: String, val explanation: String = "") {

}