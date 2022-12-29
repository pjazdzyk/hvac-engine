const serviceHost = "localhost";
const servicePort = "8002";
const apiVersion = "api/v1";
const moistAirEndpoint = "properties/air"
const restApiMoistAirUrl = `http://${serviceHost}:${servicePort}/${apiVersion}/${moistAirEndpoint}`; 

export function clearField(inputField) {
  inputField.value = "";
}

export function registerClickAction(buttonId, action) {
  let fillRandomButton = document.getElementById(buttonId);
  fillRandomButton.addEventListener("click", action);
}

export function registerChangeAction(sourceElementId, action) {
  let choiceBox = document.getElementById(sourceElementId);
  choiceBox.addEventListener("change", action);
}

export function registerFocusAction(sourceElementId, action) {
  let choiceBox = document.getElementById(sourceElementId);
  choiceBox.addEventListener("foucs", action);
}

export function registerFocusForInputsOfType(sourceElementId, inputElementType) {
  let form = document.getElementById(sourceElementId);
  let inputs = form.getElementsByTagName(inputElementType);
  for (const input of inputs) {
    input.onfocus = () => {
      input.select();
    };
  }
}

console.log(restApiMoistAirUrl);