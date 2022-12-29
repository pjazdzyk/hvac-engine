
import {
  registerChangeAction,
  registerFocusForInputsOfType
} from './shared-scripts.js';

//Disabling or enabling fields depending on selected core parameters input
function optionChangeAction() {
  var option = document.getElementById('air-prop-options').value;
  if (option === 'RH') {
    document.getElementById('dryBulbTemperature').disabled = false;
    document.getElementById('relativeHumidity').disabled = false;
    document.getElementById('humidityRatioX').disabled = true;
  } else if (option === 'x') {
    document.getElementById('dryBulbTemperature').disabled = false;
    document.getElementById('relativeHumidity').disabled = true;
    document.getElementById('humidityRatioX').disabled = false;
  }
}

function test(){

}


// Context calls
registerChangeAction("air-prop-options", optionChangeAction);
registerFocusForInputsOfType("air-prop-form", "input");