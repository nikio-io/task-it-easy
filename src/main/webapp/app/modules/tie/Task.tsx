import React from 'react';

import { connect } from 'react-redux';
import { Row, Col, Alert } from 'reactstrap';

export interface ITaskProp {
  tasks: ITask[];
}

export interface ITask {
  id: number;
  name: string;
}

export class Task extends React.Component<ITaskProp> {
  render() {
    const { tasks } = this.props;

    return (
      <Row>
        <Col md="9">
          <ul>
            {this.props.tasks.map(task => (
              <li key={task.id}>{task.name}</li>
            ))}
          </ul>
        </Col>
      </Row>
    );
  }
}

export default connect()(Task);
