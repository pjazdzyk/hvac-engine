export function clearField(inputField){
    inputField.value="";
}

export function registerClickAction(buttonId, action){
    let fillRandomButton = document.querySelector(buttonId);
    fillRandomButton.addEventListener("click", action);
}

export function registerToggleClickAction(actionSourceItemId, toggleElementId, cssClassName){
    let toggleButton = document.getElementById("toggle-button");
    let naviList = document.getElementById("navi-list");
    toggleButton.addEventListener('click', () => {
      naviList.classList.toggle("active");
    })
}