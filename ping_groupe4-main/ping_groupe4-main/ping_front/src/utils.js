import axios from "axios";

const url = "http://localhost:8080"

function getContent(filename) {
  let content = "";

  const data = JSON.stringify({
    "node": filename
  });
  console.log(data);
  let stat = 0;
  axios
      .post(url+"/nodeService/read", data)
      .then((response) => {console.log(response); content = JSON.parse(response)});
  
    return content;
}


export function getFileParam(fileName) {
    //appel d'un endpoint parcequ'on peut ap faire ca en java
    let content = getContent(fileName)
    let s = fileName.split(".");
    let language = ""
    switch (s[s.length - 1]) {
        case "js":
            language = "javascript"
            break;
        case "css":
            language = "css"
            break;
        case "html":
            language = "xml"
            break;
        case "java":
            language = "java"
            break;
        case "default":
          break;
    }

    return [language, content];

}

export function saveFile(fileName, content) {
    //utilise le endpoint update
    const data = JSON.stringify({
        node: fileName,
        from: 0,
        to: content.length,
        insertedContent: content
      });
      let stat = 0;
      axios
      .post(url+"/nodeService/update", data)
      .then((response) => {console.log(response)});

      return 200;
}


export function getUsers() {

  let users = []

  axios
      .get(url+"/users")
      .then((response) => {console.log(response)})
  
  

    return users;

}