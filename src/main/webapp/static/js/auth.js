window.addEventListener('DOMContentLoaded', (event) => {

    const username = document.getElementById("username");
    const password = document.getElementById("reg-password");
    const repeatPassword = document.getElementById("reg-rep-password");

    username.addEventListener("keydown", checkKeys);
    // repeatPassword.addEventListener("focusout", checkPasswords);

    function checkPasswords() {
        if(password.value.trim() !== repeatPassword.value.trim()){
            alert("Passwords don't match")
        }
    }

    function checkKeys(event){
        if (event.keyCode == 8 || (event.keyCode >= 48 &&  event.keyCode <= 58))
            return true;
        else
            return false;
    }
});