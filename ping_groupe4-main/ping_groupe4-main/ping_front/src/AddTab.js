import 'react';
import "Tab.css";

const AddTabButton = ({ onClick }) => {
  return (
    <button className="Tab" onClick={onClick}>
      Ajouter un onglet
    </button>
  );
};

export default AddTabButton;