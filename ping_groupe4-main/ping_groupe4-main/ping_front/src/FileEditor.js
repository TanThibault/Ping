import "codemirror";
import { Controlled as CodeMirror } from "react-codemirror2-react-17";
import "codemirror/lib/codemirror.css";

import "codemirror/mode/javascript/javascript.js";
import "codemirror/mode/xml/xml";
import "codemirror/mode/css/css";
import "codemirror/addon/edit/matchbrackets.js";
import "codemirror/addon/edit/closebrackets.js";
import "codemirror/addon/lint/lint.js";

import "codemirror/theme/abbott.css"

import "codemirror/addon/hint/show-hint.js";
import "codemirror/addon/hint/show-hint.css";
import "codemirror/addon/hint/javascript-hint.js";
import "codemirror/addon/hint/html-hint.js";
import "codemirror/addon/hint/xml-hint.js";
import "codemirror/addon/hint/css-hint.js";

import { getFileParam, saveFile } from "./utils.js"

import { useState, useEffect, useRef } from "react";


const FileEditor = (param) => {
  // language = javascript, css, xml, java
  // currentContent = contenu actuel du fichier
  const filename = param.filename;
  const language = param.language;
  const currentContent = param.content;
  const onUpdate = param.onUpdate;
  //const [filename, language, currentContent, onUpdate] = param;
  //let language = "javascript";
  //let currentContent = "eeee"
  const [content, setContent] = useState(currentContent);

  const save = () => {
    console.log("sauveagrding");
    saveFile(filename, content);
    //ecritdanslefichier(content);
  }
  // faut faire un truc ou on check l'extention du fichier pour mettre le bon language
  const options = {
    // langage et theme
    mode: {name: language, globalVars: true},
    theme: "abbott",
    direction: "ltr",

    //line config
    lineNumbers: true,
    lineWrapping: true,

    // auto
    matchBrackets: true,
    autoCloseBrackets: true,
    smartIndent: true,
    indentWithTabs: true,
    indentUnit: 4,
    electricChars: true,
    hint: CodeMirror.hint,

    extraKeys: {"Ctrl-S": save},
    // ide de base
    keyMap: "default",
    lint: true
  };

  const handleEditorChange = (editor, data, value) => {
    setContent(value);
    onUpdate(value);

    if (value[-1] != ";")
      editor.showHint({ completeSingle: false });
  }; 

  

          //autoCursor={true}
  return (
    <div style={{position: "absolute", top:50, left:0, width:'100%'}}>
        <CodeMirror className="codeMirror" 
          value={content}
          options={options}
          onBeforeChange={handleEditorChange}
          onChange={() => {}}
        />
    </div>
  );
};

export default FileEditor;