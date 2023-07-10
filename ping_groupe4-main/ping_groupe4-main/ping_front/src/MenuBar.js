import React, { useState, useRef, useEffect } from "react";
import Toolbar from "@mui/material/Toolbar";
import Button from "@mui/material/Button";
import PeopleIcon from "@mui/icons-material/People";
import Badge from "@mui/material/Badge";
import "./App.css";
import { ListItem, Menu, MenuItem, SwipeableDrawer } from "@mui/material";
import axios from "axios";
import { store, useGlobalState } from "state-pool";
import TreeBar from "./Tree";
import { Box, AppBar, List } from "@mui/material";
import { createTheme, ThemeProvider } from "@mui/material/styles";
import TreeView from "@mui/lab/TreeView";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import ChevronRightIcon from "@mui/icons-material/ChevronRight";
import TreeItem from "@mui/lab/TreeItem";
import FileEditor from "./FileEditor";
import { Switch, Drawer } from "@mui/material/";
import FormControlLabel from "@mui/material/FormControlLabel";
import Terminal, {
  ColorMode,
  OutputType,
  TerminalOutput,
  TerminalInput,
} from "react-terminal-ui";

import HelpIcon from "@mui/icons-material/Help";

import PropTypes from "prop-types";
import Tabs from "@mui/material/Tabs";
import Tab from "@mui/material/Tab";
import { getFileParam } from "./utils";
import CloseIcon from "@mui/icons-material/Close";

import Typography from "@mui/material/Typography";
import IconButton from "@mui/material/IconButton";
import MenuIcon from "@mui/icons-material/Menu";
import { saveFile } from "./utils";
import CenterFocusStrongIcon from "@mui/icons-material/CenterFocusStrong";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import ListItemText from "@mui/material/ListItemText";
import ListItemAvatar from "@mui/material/ListItemAvatar";
import Avatar from "@mui/material/Avatar";

function CustomTabPanel(props) {
  const { children, value, index, ...other } = props;

  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`simple-tabpanel-${index}`}
      aria-labelledby={`simple-tab-${index}`}
      {...other}
    >
      {value === index && (
        <Box sx={{ p: 3 }}>
          <Typography>{children}</Typography>
        </Box>
      )}
    </div>
  );
}

CustomTabPanel.propTypes = {
  children: PropTypes.node,
  index: PropTypes.number.isRequired,
  value: PropTypes.number.isRequired,
};

function a11yProps(index) {
  return {
    id: `simple-tab-${index}`,
    "aria-controls": `simple-tabpanel-${index}`,
  };
}

const MenuBar = () => {
  const [value, setValue] = React.useState(0);
  const [tabs, setTabs] = useState([
    {
      id: 0,
      title: "untitled",
      language: "javascript",
      content: "...",
    },
  ]);
  const [tabContents, setTabContents] = useState([{ id: 0, content: "..." }]);

  const handleChange = (event, newValue) => {
    setValue(newValue);
  };

  //const [tabContents, setTabContents] = useState([{ id: 1, content: "1" }, { id: 2, content: "2" }, { id: 0, content: "3" }]);

  const updateTabContent = (tabId, newContent) => {
    setTabContents(
      tabContents.map((tab) => {
        if (tab.id === tabId) {
          return { ...tab, content: newContent };
        }
        return tab;
      })
    );
  };

  const addTab = (fileName) => {
    console.log("add tab = " + fileName);
    const [language, content] = getFileParam(fileName);
    const newTab = {
      id: tabs.length,
      title: fileName,
      language: language,
      content: content,
    };
    setTabContents(
      tabContents.concat([{ id: newTab.id, content: newTab.content }])
    );
    setTabs([...tabs, newTab]);
  };

  const removeTab = (tabId) => {
    setTabs(tabs.filter((tab) => tab.id !== tabId));
    setTabContents(tabContents.filter((tab) => tab.id !== tabId));
  };

  const param = ["javascript", "..."];
  const [tree, setTree] = useState(null);
  const [treeView, setTreeView] = useState(null);
  const makeTree = (fileList) => {
    const mtree = {};
    for (const file of fileList) {
      const path = file.webkitRelativePath.split("/");
      let currentNode = mtree;

      for (let i = 0; i < path.length; i++) {
        const segment = path[i];

        if (!currentNode[segment]) {
          currentNode[segment] = {
            id: segment,
            name: segment,
            path: path.slice(0, i + 1).join("/"),
            children: {},
          };
        }

        currentNode = currentNode[segment].children;
      }
    }
    return mtree;
  };
  let id = 0;
  const makeTreeView = (nodes) => {
    if (nodes == null) {
      return null;
    }
    id += 1;
    console.log("making treeView", nodes);
    return Object.values(nodes).map((node) => (
      <TreeItem
        key={node.path}
        nodeId={id.toString()}
        label={node.name}
        onClick={() => addTab(node.path)}
      >
        {makeTreeView(node.children)}
      </TreeItem>
    ));
  };

  const [anchorEl, setAnchorEl] = useState(null);
  const [anchorElgit, setAnchorElgit] = useState(null);

  const openfile = Boolean(anchorEl);
  const opengit = Boolean(anchorElgit);

  const handleMenu = (event) => {
    setAnchorEl(event.currentTarget);
  };
  const handleMenuGit = (event) => {
    setAnchorElgit(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };
  const handleNew = () => {
    setAnchorEl(null);
  };
  const inputRef = useRef(null);
  const inputRefFolder = useRef(null);
  const [currentFile, setCurrentFile] = useState(null);
  const handleOpen = () => {
    inputRef.current.click();
    setAnchorEl(null);
  };
  const handleOpenFolder = () => {
    inputRefFolder.current.click();
    setAnchorEl(null);
  };
  const handleFileChange = (event) => {
    console.log(event.target);
    const fileObj = event.target.files && event.target.files[0];
    if (!fileObj) {
      return;
    }
    event.target.value = null;
    console.log("files:", fileObj);

    const dt = new DataTransfer();
    dt.items.add(fileObj);
    setCurrentFile(dt.files);
    console.log(currentFile);
    setTree(makeTree(dt.files));
    setTreeView(<TreeItem key="1" nodeId="1" label={fileObj.name}></TreeItem>);
  };
  const handleFolderChange = (event) => {
    console.log(event.target);
    const fileObj = event.target.files && event.target.files;
    if (!fileObj) {
      return;
    }
    event.target.value = null;
    console.log("files:", fileObj);
    setCurrentFile(fileObj);
    console.log(currentFile);
    setTree(makeTree(fileObj));
    setTreeView(makeTreeView(makeTree(fileObj)));
  };

  const handleSave = () => {
    setAnchorEl(null);
  };
  const handleExit = () => {
    setAnchorEl(null);
    // window.close();
    console.log(tree);
  };
  const handleCloseGit = () => {
    setAnchorElgit(null);
  };
  const handleGitPull = () => {
    axios
      .get("http://localhost:8080/git/pull")
      .then((response) => console.log(response));
    setAnchorElgit(null);
  };
  const handleGitPush = () => {
    axios
      .get("http://localhost:8080/git/push")
      .then((response) => console.log(response));
    setAnchorElgit(null);
  };
  const handleGitAdd = (file) => {
    const body = '[{"path":' + currentFile.current.name + "}]";
    axios
      .post("http://localhost:8080/git/add", JSON.parse(body))
      .then((response) => console.log(response));
    setAnchorElgit(null);
  };
  const handleGitCommit = () => {
    const body = '{"message":"fix: message"}';
    axios
      .post("http://localhost:8080/git/commit", JSON.parse(body))
      .then((response) => console.log(response));
    setAnchorElgit(null);
  };
  const [invisible, setInvisible] = React.useState(false);

  const handleBadgeVisibility = () => {
    setInvisible(!invisible);
  };
  const theme = createTheme({
    palette: {
      primary: {
        main: "#a68080",
      },
      secondary: {
        main: "#758C74",
      },
      success: {
        main: "#FF0000",
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
    overrides: {
      MuiTreeItem: {
        label: {
          fontSize: "10px",
        },
      },
    },
  });
  const makeUserhelp = (user, id) => {
    return (
      <ListItem id={id}>
        <ListItemAvatar>
          <Avatar>
            <HelpIcon />
          </Avatar>
        </ListItemAvatar>
        <ListItemText primary={user.name} />
      </ListItem>
    );
  };
  const makeUsersfocus = (user, id) => {
    return (
      <ListItem id={id}>
        <ListItemAvatar>
          <Avatar>
            <CenterFocusStrongIcon />
          </Avatar>
        </ListItemAvatar>
        <ListItemText primary={user.name} />
      </ListItem>
    );
  };
  const makeUsersconnect = (user, id) => {
    console.log(user);
    return (
      <ListItem id={id}>
        <ListItemAvatar>
          <Avatar>
            <CheckCircleIcon />
          </Avatar>
        </ListItemAvatar>
        <ListItemText primary={user.name} />
      </ListItem>
    );
  };
  const [posts, setPosts] = useState([]);
  useEffect(() => {
    async function getUser() {
      // faire la request pour voir
      let userslist = new Array();
      // exemple de user
      await axios.get("http://localhost:8080/users").then((response) => {
        let out = response.data;
        let outuser = out.users;
        outuser.forEach((u) => {
          let us = { id: u.id, name: u.name, status: u.status };
          userslist.push(us);
        });
      });
      console.log(userslist);
      let id = 0;
      setPosts(
        <List
          sx={{ width: "100%", maxWidth: 360, bgcolor: "background.paper" }}
        >
          {userslist.map((user) => {
            id += 1;
            if (user.status == "HELP") {
              return makeUserhelp(user);
            } else if (user.status == "FOCUS") {
              return makeUsersfocus(user);
            } else {
              return makeUsersconnect(user);
            }
          })}
        </List>
      );
    }
    getUser();
  }, [invisible]);

  const [terminalOpen, setTerminal] = React.useState(false);
  const [statusOpen, setStatus] = React.useState(false);
  const [statusOpen2, setStatus2] = React.useState(false);
  const handleFocus = async () => {
    // envoyer status utilisateur focus a la base de donnee
    let body;
    if (!statusOpen) {
      body = '{"status":"FOCUS"}';
    } else {
      body = '{"status":"CONNECTED"}';
    }
    await axios.post("http://localhost:8080/status", JSON.parse(body));
    setStatus(!statusOpen);
    setStatus2(false);
    setAnchorElgit(null);
  };

  const handleHelp = async () => {
    // envoyer status utilisateur help a la base de donnee
    let body;
    if (!statusOpen2) {
      body = '{"status":"HELP"}';
    } else {
      body = '{"status":"CONNECTED"}';
    }
    await axios.post("http://localhost:8080/status", JSON.parse(body));
    setStatus2(!statusOpen2);
    setStatus(false);
    setAnchorElgit(null);
  };

  const handleTerminal = () => {
    setTerminal(!terminalOpen);
    setAnchorElgit(null);
  };

  const [lineData, setLine] = React.useState("Type command");

  const handleInput = (inputText) => {
    let ld = [...lineData];
    ld.push(<TerminalInput>{inputText}</TerminalInput>);
    let outputText = `command ${inputText}`;
    ld.push(<TerminalOutput>{outputText}</TerminalOutput>);
    setLine(ld);
  };

  const handleRun = async (inputText) => {
    setTerminal(true);
    const body = `{"pseudo":"${inputPseudo}"}`;
    let a = await axios
      .post("http://localhost:8080/maven/exec", JSON.parse(body))
      .then((response) => console.log(response));
    let ld = [...lineData];
    ld.push(a.data);
    setLine(ld);
  };
  const [inputPseudo, setPseudo] = React.useState("");
  const [textVisible, setTextVisible] = React.useState(true);

  const handlePseudo = async (event) => {
    if (event.key === "Enter") {
      const body = `{"pseudo":"${inputPseudo}"}`;

      await axios
        .post("http://localhost:8080/connection", JSON.parse(body))
        .then((response) => console.log(response));
      setTextVisible(false);
      setAnchorElgit(null);
    }
  };
  return (
    <div className="MenuBar">
      <Box>
        <ThemeProvider theme={theme}>
          <AppBar color={"primary"}>
            <div className="Title">
              <h2>IDE du Bosphore</h2>
            </div>
            <Toolbar>
              <Button
                sx={{ fontSize: "1rem", color: "white" }}
                aria-controls={openfile ? "menu-file" : undefined}
                aria-haspopup="true"
                aria-expanded={openfile ? "true" : undefined}
                onClick={handleMenu}
              >
                File
              </Button>
              <Menu
                id="menu-file"
                anchorEl={anchorEl}
                transformOrigin={{ horizontal: "right", vertical: "top" }}
                anchorOrigin={{ horizontal: "right", vertical: "bottom" }}
                open={Boolean(anchorEl)}
                onClose={handleClose}
                onClick={handleClose}
                postition="fixed"
              >
                <MenuItem onClick={handleNew}>New</MenuItem>
                <MenuItem onClick={handleOpen}>Open File</MenuItem>
                <MenuItem onClick={handleOpenFolder}>Open Folder</MenuItem>
                <MenuItem onClick={handleSave}>Save</MenuItem>
                <MenuItem onClick={handleExit}>Exit</MenuItem>
              </Menu>
              <Button
                sx={{ fontSize: "1rem", color: "white" }}
                aria-controls={opengit ? "menu-git" : undefined}
                aria-haspopup="true"
                aria-expanded={opengit ? "true" : undefined}
                onClick={handleMenuGit}
              >
                Git
              </Button>
              <Menu
                id="menu-git"
                anchorEl={anchorElgit}
                open={Boolean(anchorElgit)}
                onClose={handleCloseGit}
                onClick={handleCloseGit}
                transformOrigin={{ horizontal: "right", vertical: "top" }}
                anchorOrigin={{ horizontal: "right", vertical: "bottom" }}
              >
                <MenuItem onClick={handleGitPull}>Pull</MenuItem>
                <MenuItem onClick={handleGitAdd}>Add</MenuItem>
                <MenuItem onClick={handleGitCommit}>Commit</MenuItem>
                <MenuItem onClick={handleGitPush}>Push</MenuItem>
              </Menu>
              <Button
                sx={{ fontSize: "1rem", color: "white" }}
                onClick={handleRun}
              >
                Run
              </Button>
              <Button
                sx={{ fontSize: "1rem", color: "white" }}
                onClick={handleTerminal}
              >
                Terminal
              </Button>
              {terminalOpen && (
                <div
                  style={{
                    backgroundColor: "#000",
                    color: "#fff",
                    padding: "1rem",
                    fontFamily: "monospace",
                    position: "fixed",
                    bottom: 75,
                    left: 200,
                    minHeight: "200px",
                    maxHeight: "400px",
                    minWidth: "500px",
                    maxWidth: "600px",
                    overflow: "auto",
                  }}
                >
                  <Terminal name="Terminal" onInput={handleInput}>
                    {lineData}
                  </Terminal>
                </div>
              )}

              {!statusOpen && (
                <Button onClick={handleBadgeVisibility} sx={{ left: 480 }}>
                  <Badge
                    sx={{ float: "right" }}
                    variant="dot"
                    color="secondary"
                  >
                    <PeopleIcon
                      color="action"
                      size="large"
                      edge="end"
                      aria-label="menu-collab"
                      aria-haspopup="true"
                    />
                  </Badge>
                  <Drawer
                    anchor="right"
                    open={invisible}
                    onClose={handleBadgeVisibility}
                    onOpen={handleBadgeVisibility}
                  >
                    {posts}
                  </Drawer>
                </Button>
              )}

              <FormControlLabel
                control={
                  <Switch
                    color="success"
                    size="medium"
                    checked={statusOpen}
                    onChange={handleFocus}
                  />
                }
                labelPlacement="start"
                label="Focus"
              />

              <FormControlLabel
                control={
                  <Switch
                    color="success"
                    size="medium"
                    checked={statusOpen2}
                    onChange={handleHelp}
                  />
                }
                labelPlacement="start"
                label="Help"
              />

              {textVisible && (
                <div>
                  <input
                    type="text"
                    value={inputPseudo}
                    onChange={(event) => setPseudo(event.target.value)}
                    onKeyDown={handlePseudo}
                  />
                </div>
              )}

              <input
                id="files"
                ref={inputRef}
                type="file"
                style={{ display: "none" }}
                onChange={handleFileChange}
              />
              <input
                id="folder"
                ref={inputRefFolder}
                type="file"
                style={{ display: "none" }}
                onChange={handleFolderChange}
                webkitdirectory="true"
                directory="true"
                mozdirectory="true"
              />
            </Toolbar>
          </AppBar>
        </ThemeProvider>
      </Box>
      <div className="Tree" style={{ display: "flex" }}>
        <div className="TreeChild">
          {tree && (
            <TreeView
              aria-label="file system navigator"
              defaultCollapseIcon={<ExpandMoreIcon />}
              defaultExpandIcon={<ChevronRightIcon />}
              sx={{
                position: "relative",
                backgroundColor: "#D9D6D2",
                height: 400,
                width: 200,
                flexGrow: 1,
                flex: "30%",
                maxWidth: 200,
                maxHeight: 400,
                top: 95,
                overflowY: "auto",
                paddingBottom: "1rem",
                fontSize: "0.2rem",
                "& .MuiTreeItem-label": {
                  fontSize: "0.1rem",
                },
              }}
            >
              {tree && treeView}
            </TreeView>
          )}
        </div>
        <div>
          <div className="EditorBox">
            <Box sx={{ position: "absolute", top: "100px", width: "100%" }}>
              <Box sx={{ borderBottom: 1, borderColor: "divider" }}>
                <Tabs
                  className="tt"
                  value={value}
                  onChange={handleChange}
                  aria-label="basic tabs example"
                >
                  {tabs.map((tab) => (
                    <Tab
                      key={tab.id}
                      label={
                        <div>
                          {tab.title}
                          <IconButton
                            className="close-button"
                            aria-label="Close"
                            onClick={() => removeTab(tab.id)}
                            size="small"
                          >
                            <CloseIcon fontSize="small" />
                          </IconButton>
                        </div>
                      }
                      {...a11yProps(tab.id)}
                    />
                  ))}
                </Tabs>
              </Box>
              {tabs.map((tab) => {
                const tabContent = tabContents.find(
                  (content) => content.id === tab.id
                );
                return (
                  <CustomTabPanel value={value} index={tab.id} key={tab.id}>
                    <div>
                      {tabContent && (
                        <FileEditor
                          filename={tab.title}
                          language={tab.language}
                          content={tabContent.content}
                          onUpdate={(newContent) => {
                            updateTabContent(tab.id, newContent);
                          }}
                        />
                      )}
                    </div>
                  </CustomTabPanel>
                );
              })}
              <button
                style={{
                  position: "absolute",

                  width: 40,
                  height: 50,
                }}
                onClick={() => addTab("name")}
              />
            </Box>
          </div>
        </div>
      </div>
    </div>
  );
};
export default MenuBar;
