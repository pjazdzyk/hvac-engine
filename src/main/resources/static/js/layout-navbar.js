
import {
  registerToggleClickAction
} from './shared-scripts.js';

// Burger menu button toggle: show and hide menu items
registerToggleClickAction("toggle-button", "navi-list", "active");

function navbarToggleAction(){
  let toggleButton = document.getElementById("toggle-button");
  let naviList = document.getElementById("navi-list");
  naviList.classList.toggle("active");
}

function registerToggleClickAction(actionSourceItemId, toggleElementId, cssClassName){

  toggleButton.addEventListener('click', () => {
    naviList.classList.toggle("active");
  })
}