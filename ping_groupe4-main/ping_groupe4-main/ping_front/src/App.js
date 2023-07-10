import "./App.css";
import FileEditor from "./FileEditor";
import MenuBar from "./MenuBar";
import { createTheme, ThemeProvider } from "@mui/material/styles";
import TreeBar from "./Tree";
import React, { useState, useEffect } from "react";
import TimeDate from "./date";
import {
  Button,
  Typography,
  Toolbar,
  Box,
  AppBar,
  Container,
} from "@mui/material";

import BasicTabs from './Tabz';
import Sidebar from './SideBar';

function App() {
  
  const [code, setCode] = useState('');

  const [content, setContent] = useState("..");

  const handleCodeChange = (newCode) => {
    setCode(newCode);
  };

  //<div >{Editor(code, handleCodeChange)}</div>

  const theme = createTheme({
    palette: {
      primary: {
        main: "#a68080",
      },
      secondary: {
        main: "#758C74",
      },
    },
    components: {
      styleOverrides: {
        dense: {
          height: 20,
          minHeight: 20,
        },
      },
    },
  });
  const [luminosity, setLum] = useState(1);

  useEffect(() => {
    const lum = () => {
      var hour = new Date().getHours();

      if (hour >= 6 && hour < 9) {
        setLum(0.8);
      } else if (hour >= 9 && hour < 17) {
        setLum(1);
      } else if (hour >= 17 && hour < 21) {
        setLum(0.6);
      } else setLum(0.4);
    };
    lum();
    const interval = setInterval(lum, 3600000);

    return () => {
      clearInterval(interval);
    };
  }, []);

  return (
    <div className="App" style={{ filter: `brightness(${luminosity})` }}>
      <div className="FileEditor">
        {MenuBar()}
        <TimeDate/>
      </div> </div>);
}

export default App;
