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

export function getAllActiveFieldsFromFormAsMap(formId) {
  let form = document.getElementById(formId);
  let inputsMap = new Map();
  for (let i = 0; i < form.elements.length; i++) {
    let input = form.elements[i];
    if (input.tagName === 'INPUT' && !input.readOnly && !input.disabled) {
      inputsMap.set(input.id, input.value);
    }
  }
  return inputsMap;
}

export function getAllInactiveFieldsAsList(formId) {
  let form = document.getElementById(formId);
  let inputs = [];
  for (let i = 0; i < form.elements.length; i++) {
    let input = form.elements[i];
    if (input.tagName === 'INPUT' && (input.readOnly || input.disabled)) {
      inputs.push(input);
    }
  }
  return inputs;
}

export function clearAllInactiveFields(formId){
  let inputs = getAllInactiveFieldsAsList(formId);
  for (const element of inputs) {
    element.value = "";
  }
}

export async function makeGetApiCall(baseUrl, params) {
  let queryParams = new URLSearchParams(params);
  let url = `${baseUrl}?${queryParams.toString()}`;
  let callMode = {
    method: "GET",
    mode: "cors"
  }
  let response = await fetch(url, callMode);
  if (response.status !== 200) {
    throw new Error(`Failed to load API: ${response.status}`);
  }
  
  return await response.json();
}

export function updateFormFromJson(formId, data) {
  let form = document.getElementById(formId);
  let inputs = form.getElementsByTagName('INPUT');
  for (const element of inputs) {
    let inputId = element.id;
    if (data.hasOwnProperty(inputId)) {
      element.value = data[inputId];
    }
  }
}