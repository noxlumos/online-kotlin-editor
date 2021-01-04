import './App.css';
import React from 'react';
import 'semantic-ui-css/semantic.min.css';
import { Header, Grid, Icon, Menu, Loader, Popup } from 'semantic-ui-react';
import ContentEditable from 'react-contenteditable';
import axios from 'axios';
import Editor from 'react-simple-code-editor';



class KotlinIDE extends React.Component {
  
    constructor(props) {

      super(props)
      this.contentEditable = React.createRef();
      this.state = { script: "",
                     code: 'println("Hello, World!")',
                     output: "",
                     compiled: false,
                     running: false,
                     keywords: new Map([["var", "#25F98A"], ["val", "#25F98A"],
                                        ["forEach", "#FF42FB"], ["for", "#FF42FB"], ["continue", "#FF42FB"],["break", "#FF42FB"],["while", "#FF42FB"],["return", "#FF42FB"],
                                        ["if", "#2FFFFF"], ["else", "#2FFFFF"],
                                        ["IntArray","#F7FF6F"], ["Int","#F7FF6F"], ["String","#F7FF6F"],["Array","#F7FF6F"],
                                        ["class","#FF5D3B"], ["interface","#FF5D3B"],
                                        ]),
                      compileColors: new Map([["Compiled successfully!", "#25F98A"], ["Failed to compile.", "#FF5D3B"], ["Compiled with warnings.", "#F7FF6F"]])                  

                    }
      this.onClick = this.onClick.bind(this);
      this.onClear = this.onClear.bind(this);
      this.highlight = this.highlight.bind(this);
    }

    getOuput() {
      const eventSource = new EventSource('http://localhost:8080/getOutput'); 
      eventSource.onopen = (event) => console.log('open', event);
      eventSource.onmessage = (event) => {
        var output = JSON.parse(event.data).source
        if(output.streamName === "inputStream" & output.outputLine === "finished"){
          this.setState({running: false})
          eventSource.close()
        }
        else if(output.streamName === "errorStream" & output.outputLine !== "finished"){
          if(output.outputLine.match("error") !== null){
            this.setState({compiled: "Failed to compile."})
          }
          else if(output.outputLine.match("WARNING")!== null){
            this.setState({compiled: "Compiled with warnings."})
          }
          this.setState({output: this.state.output + output.outputLine + "<br>"})
        }
        else if(output.outputLine !== "finished")
          this.setState({output: this.state.output + output.outputLine + "<br>"})
      };
      eventSource.onerror = (event) => console.log('error', event);
    }

    highlight(code){
      
      this.state.keywords.forEach((value, key)=>{
        code = code.replace(new RegExp('(?<![a-z])' + key+ '(?!</span>)', 'g'), "<span style='color:" + value + "'>" + key + "</span>")
      })
      var quotos = code.match(new RegExp('"([^"]*)"', 'g'))
      if (quotos !== null) {
        quotos.forEach(quote =>{
          code = code.replace(quote, "<span style='color: #2FFFFF '>" + quote + "</span>")
        })
      } 
      return(code)
    }

    onClick(){
      this.setState({running: true})
      this.setState({compiled: "Compiled successfully!"}) 
      var script = encodeURIComponent(this.state.code)
      console.log(script)
      this.getOuput()
      axios.post('http://localhost:8080/script', null, { params: {script}})
    }

    onClear(){
      this.setState({output: "", compiled: ""})
    }

    render() {
      return(
        <Grid divided inverted padded style={{height: '100vh'}}>
          <Grid.Row columns={1} centered style={{height: '5%'}}>
            <Grid.Column>
              <Header as='h3' block textAlign='center'>
                Kotlin Editor
              </Header>
            </Grid.Column>
          </Grid.Row >
          <Grid.Row columns={2} style={{height: '95%'}}>
            <Grid.Column>
              <Menu icon borderless>
                <Menu.Item
                  name="play"
                  onClick={this.onClick}
                  disabled={this.state.running}
                  >
                    <Icon name='play' />
                </Menu.Item>
              </Menu>
                <Editor value={this.state.code}
                        onValueChange={code => this.setState({ code})}
                        highlight={code => this.highlight(code)}
                        padding={10}
                        textareaClassName={'script'}
                        style={{
                          fontFamily: '"Fira code", "Fira Mono", monospace',
                          fontSize: 16,
                          height: "95%",
                          backgroundColor: "#404040",
                          color: "white",
                        }}/>
            </Grid.Column>
            <Grid.Column>
              <Menu icon borderless>
                <Menu.Item
                  name="loader"
                  active={this.state.running}
                  >
                    {this.state.running ? 
                      <Loader size='mini' active={this.state.running} inline /> : 
                      this.state.compiled }
                </Menu.Item>
                <Menu.Item
                  name="clear"
                  onClick={this.onClear}
                  position="right"
                  >
                     <Popup content='Clear output pane' trigger={<Icon name='eraser'/>} />
                    
                </Menu.Item>
              </Menu>      
              <ContentEditable
                html={this.state.output} // innerHTML of the editable div
                disabled={true}       // use true to disable editing
                tagName='article' // Use a custom HTML tag (uses a div by default)
                className="script"
              />
            </Grid.Column>
          </Grid.Row>
          <span text = "deneme"/>
        </Grid>
    )};
}

export default KotlinIDE;
