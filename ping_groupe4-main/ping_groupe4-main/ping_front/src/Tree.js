import React from "react";
import TreeView from "@mui/lab/TreeView";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import ChevronRightIcon from "@mui/icons-material/ChevronRight";
import TreeItem from "@mui/lab/TreeItem";
import FileEditor from "./FileEditor";

const TreeBar = (directoryString) => {
  const param = ["javascript", "..."];
  const makeTree = (path) => {
    if (path == null) {
      return null;
    }
    const pathSplit = path.split('/').filter(segment => segment !== '');
    let currentPath = '/';
    let treeData = [];

    for (let i = 0; i < pathSplit.length; i++) {
      const segment = pathSplit[i];
      currentPath += segment + '/';

      const treeItem = {
        id: i + 1,
        name: segment,
        path: currentPath,
        children: [],
      };

      if (i === 0) {
        treeData.push(treeItem);
      } else {
        const parent = treeData[i - 1];
        parent.children.push(treeItem);
      }
    }

    return treeData;
  };
  const makeTreeView = (nodes) =>
    {
      if (nodes == null) {
        return null;
      }
    nodes.map((node) => (
      <TreeItem key={node.id} nodeId={node.path} label={node.name}>
        {Array.isArray(node.children) ? makeTreeView(node.children) : null}
      </TreeItem>
    ))};
  return (
    <div className="Tree" style={{ display: "flex" }}>
      <div className="TreeChild">
        <TreeView
          aria-label="file system navigator"
          defaultCollapseIcon={<ExpandMoreIcon />}
          defaultExpandIcon={<ChevronRightIcon />}
          sx={{
            position: "relative",
            backgroundColor: "#D9D6D2",
            height: 400,
            flexGrow: 1,
            flex: "30%",
            maxWidth: 150,
            maxHeight: 400,
            top: 95,
            overflowY: "auto",
            paddingBottom: "1rem",
            fontSize: "0.6rem",
            "& .MuiTreeItem-label": {
              fontSize: "0.5rem",
            },
          }}
        >
          {makeTreeView(makeTree(directoryString))};
        </TreeView>
      </div>
      <div
        className="EditorBox"
        style={{
          fontFamily: "sans-serif",
          textAlign: "left",
          left: "150px",
          height: "50vh",
          width: "70%",
          flex: "70%",
          top: 95,
          background: "#a68080",
          border: "none",
          position: "relative",
        }}
      >
        {FileEditor(param)}
      </div>
    </div>
  );
};
export default TreeBar;
