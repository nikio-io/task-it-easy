import React from 'react';

import { connect } from 'react-redux';
import { Row, Col, Alert } from 'reactstrap';

import ShowItems from './ShowItems';
import AddItem from './AddItem';

import { getEntities } from 'app/entities/item/item.reducer';
import { Type } from 'app/shared/model/item.model';

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
          <h3>Have a look at your Boards</h3>

          <Row>
            <Col>
              <h3>Tasks</h3>
              <ShowItems items={this.props.itemList.filter(i => i.type === Type.TASK)} />
              <AddItem type={Type.TASK} />
            </Col>
            <Col>
              <h3>Reminder</h3>
              <ShowItems items={this.props.itemList.filter(i => i.type === Type.REMINDER)} />
              <AddItem type={Type.REMINDER} />
            </Col>
            <Col>
              <h3>Birthdays?</h3>
              <ShowItems items={this.props.itemList.filter(i => i.type === Type.BIRTHDAY)} />
              <AddItem type={Type.BIRTHDAY} />
            </Col>
          </Row>
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
