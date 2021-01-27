
 var codeEditor = CodeMirror(document.body, {
              value: "public class Test {\nint f(int n) {\n return 4;\n}\n}",
              mode:  "text/x-java",
              lineNumbers: true,
              theme: "eclipse"
            });

function getCode() {
    return codeEditor.getValue()
}