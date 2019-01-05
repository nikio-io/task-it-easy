import React from 'react';

import { connect } from 'react-redux';
import { Row, Col, Alert } from 'reactstrap';
import { IItem } from 'app/shared/model/item.model';

export interface IShowItemsProp {
  items: IItem[];
}

export class ShowItems extends React.Component<IShowItemsProp> {
  render() {
    return (
      <Row>
        <Col md="9">
          <ul>
            {this.props.items.map(item => (
              <li key={item.id}>{item.title}</li>
            ))}
          </ul>
        </Col>
      </Row>
    );
  }
}

export default connect()(ShowItems);
