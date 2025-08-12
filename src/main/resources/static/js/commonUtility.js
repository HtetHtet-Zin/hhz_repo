function disableOrEnableInput(inputElement, condition){
    if(condition){
        inputElement.disabled = true;
        inputElement.style.cursor = 'default';
    }else {
        inputElement.disabled = false;
        inputElement.style.cursor = 'pointer';
    }
}

function disableInput(inputElement){
    inputElement.disabled = true;
    inputElement.style.cursor = 'default';
}

function enableInput(inputElement){
    inputElement.disabled = false;
    inputElement.style.cursor = 'pointer';
}


