import React from 'react';

import { connect } from 'react-redux';
import { Row, Col, Alert } from 'reactstrap';

import Tasks from './Tasks';
import { getEntities } from 'app/entities/item/item.reducer';

export interface ITieProp extends StateProps, DispatchProps {}

export class Tie extends React.Component<ITieProp> {
  componentDidMount() {
    this.props.getEntities();
  }

  render() {
    return (
      <Row>
        <Col md="9">
          <h2>Welcome to TIE!</h2>

          <h3>Tasks</h3>

          <Tasks tasks={this.props.itemList} />
        </Col>
      </Row>
    );
  }
}

const mapStateToProps = storeState => ({
  itemList: storeState.item.entities
});

const mapDispatchToProps = {
  getEntities
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Tie);
