import {
  registerChangeAction,
  registerFocusForInputsOfType,
  getAllActiveFieldsFromFormAsMap,
  makeGetApiCall,
  updateFormFromJson,
  registerClickAction,
  clearAllInactiveFields
} from './shared-scripts.js';

let moistAirApiUrl = "http://localhost:8002/api/v1/properties/air";

const airFormId = "airPropForm";
const airCalcButtonId = "airCalculateButton";
const airResetButtonId = "airResetButton";
const airOptionSelecId = "airPropOptions";

const airDefaultValues = {
  "absPressure": 101300.2,
  "dryBulbTemperature": 20.5,
  "relativeHumidity": 50.5,
  "humidityRatioX": 0.00001
};

let waterApiUrl = "http://localhost:8002/api/v1/properties/water";

const waterFormId = "waterPropForm";
const waterCalcButtonId = "waterCalculateButton";
const waterResetButtonId = "waterResetButton";

const waterDefaultValues = {
  "absPressure": 101300.2,
  "temperature": 20.5,
};

// Action of disabling or enabling fields depending on selected core parameters input
function optionChangeAction() {
  var option = document.getElementById("airPropOptions").value;
  if (option === "RH") {
    document.getElementById("dryBulbTemperature").readOnly = false;
    document.getElementById("relativeHumidity").readOnly = false;
    document.getElementById("humidityRatioX").readOnly = true;
  } else if (option === "x") {
    document.getElementById("dryBulbTemperature").readOnly = false;
    document.getElementById("relativeHumidity").readOnly = true;
    document.getElementById("humidityRatioX").readOnly = false;
  }
}

// General action for calculating of any form
async function calculateFormAction(apiUrl, formId, resetButtonId) {
  let params = getAllActiveFieldsFromFormAsMap(formId);
  var resetButton = document.getElementById(resetButtonId);
  let response;
  try {
    response = await makeGetApiCall(apiUrl, params);
  } catch (error) {
    resetButton.click();
  }
  updateFormFromJson(formId, response);
}

function resetFormAction(formId, defaultValues) {
  updateFormFromJson(formId, defaultValues);
  clearAllInactiveFields(formId);
}


// Registering actions
//--AIR--
registerChangeAction(airOptionSelecId, optionChangeAction);
registerClickAction(airResetButtonId, () => resetFormAction(airFormId, airDefaultValues));
registerClickAction(airCalcButtonId, () => calculateFormAction(moistAirApiUrl, airFormId, airResetButtonId));
registerFocusForInputsOfType(airFormId, "input");

//--WATER--
registerClickAction(waterResetButtonId, () => resetFormAction(waterFormId, waterDefaultValues));
registerClickAction(waterCalcButtonId, () => calculateFormAction(waterApiUrl, waterFormId, waterResetButtonId));
registerFocusForInputsOfType(waterFormId, "input");

// Initializers
calculateMoistAirButtonAction();