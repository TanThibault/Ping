import React, { useState } from 'react';
import PropTypes from 'prop-types';
import Tabs from '@mui/material/Tabs';
import Tab from '@mui/material/Tab';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';

import FileEditor from "./FileEditor";

import "./Tab.css"
import { getFileParam } from './utils';
import { IconButton } from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';


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
    'aria-controls': `simple-tabpanel-${index}`,
  };
};

const BasicTabs = () =>{
  const [value, setValue] = React.useState(0);
  const [tabs, setTabs] = useState([{
    id: 0,
    title: "untitled",
    language: "javascript",
    content: "..."
  }]);
  const [tabContents, setTabContents] = useState([{ id: 0, content: "..." }]);

  const handleChange = (event, newValue) => {
    setValue(newValue);
  };

  //const [tabContents, setTabContents] = useState([{ id: 1, content: "1" }, { id: 2, content: "2" }, { id: 0, content: "3" }]);

  const updateTabContent = (tabId, newContent) => {
    setTabContents(tabContents.map(tab => {
      if (tab.id === tabId) {
        return { ...tab, content: newContent };
      }
      return tab;
    }));
  };

  const addTab = (fileName) => {
    const [language, content] = getFileParam(fileName);
    const newTab = {
      id: tabs.length,
      title: fileName,
      language: language,
      content: content
    };
    setTabContents(tabContents.concat([{ id: newTab.id, content: newTab.content }]))
    setTabs([...tabs, newTab]);
  };

  const removeTab = (tabId) => {
    setTabs(tabs.filter(tab => tab.id !== tabId));
    setTabContents(tabContents.filter(tab => tab.id !== tabId));
  };

  //<button onClick={ () => { setTabs(tabs.filter(x => { x.id !== tab.id })); setTabContents(tabContents.filter(x => { x.id !== tab.id })) } }></button>
  //<button onClick={ () => removeTab(tab.id)}>X</button>            

  return (
    <div className='EditorBox'>
      <Box sx={{ width: '100%' }}>
        <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
          <Tabs className='tt' value={value} onChange={handleChange} aria-label="basic tabs example">
            {tabs.map(tab => (
              <Tab key={tab.id} label={<div>
                {tab.title}
                <IconButton
                  className="close-button"
                  aria-label="Close"
                  onClick={() => removeTab(tab.id)}
                  size="small"
                >
                  <CloseIcon fontSize="small" />
                </IconButton>
              </div>} {...a11yProps(tab.id)} />

            ))}
          </Tabs>
        </Box>
        {tabs.map(tab => {
          const tabContent = tabContents.find(content => content.id === tab.id);
          return (
            <CustomTabPanel value={value} index={tab.id} key={tab.id}>
              <div>
                {tabContent && <FileEditor filename={tab.title} language={tab.language} content={tabContent.content} onUpdate={(newContent) => { updateTabContent(tab.id, newContent) }} />}
              </div>
            </CustomTabPanel>
          );
        })}

      </Box>
      <button style={{ position: "absolute", top: 0, left: 0, width: 40, height: 50 }} onClick={() => addTab("name")} />

    </div>
  );
};
export default BasicTabs;