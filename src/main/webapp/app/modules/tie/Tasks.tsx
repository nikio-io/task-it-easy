import React from 'react';

import { connect } from 'react-redux';
import { Row, Col, Alert } from 'reactstrap';
import { IItem } from 'app/shared/model/item.model';

export interface ITaskProp {
  tasks: IItem[];
}

export class Tasks extends React.Component<ITaskProp> {
  render() {
    return (
      <Row>
        <Col md="9">
          <ul>
            {this.props.tasks.map(task => (
              <li key={task.id}>{task.title}</li>
            ))}
          </ul>
        </Col>
      </Row>
    );
  }
}

export default connect()(Tasks);
