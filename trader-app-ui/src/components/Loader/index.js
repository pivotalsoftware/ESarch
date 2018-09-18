import React from 'react';
import './styles.css';

const Loader = (props) => {
    let className = 'axon-loader text-center'

    if(props.className) {
        className += ` ${props.className}`
    }

    return (
        <div className={className}/>
    );
}

export default Loader;
