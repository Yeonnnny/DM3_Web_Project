$(function() {
    
    // CKEditor 초기화 및 인스턴스 생성
    let ckEditorInstance;
    ClassicEditor.create(document.querySelector("#editor"), {
    }).then(editor => {
        // 에디터 인스턴스를 저장
        ckEditorInstance = editor;
    }).catch(error => {
        console.error(error);
    });

    $("#saveBtn").on("click",  function () {
        submitVaildation(ckEditorInstance);
    });

})