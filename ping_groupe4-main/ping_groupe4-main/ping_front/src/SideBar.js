import React from "react";
import {slide as BurgerMenu} from "react-burger-menu";
//import "react-burger-menu/lib/menus/slide.css";


const Sidebar = () => {
  return (
    <BurgerMenu>
      <ul>
        <li>Home</li>
        <li>About</li>
        <li>Contact</li>
      </ul>
    </BurgerMenu>
  );
};

export default Sidebar;