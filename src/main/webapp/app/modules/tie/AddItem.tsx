import React from 'react';

import { connect } from 'react-redux';
import { Row, Col, Alert } from 'reactstrap';
import { IItem, Type } from 'app/shared/model/item.model';
import { IRootState } from 'app/shared/reducers';
import { createEntity } from 'app/entities/item/item.reducer';

export interface IAddTaskProp extends StateProps, DispatchProps {
  type: Type;
}

export interface IAddTaskState {
  task: IItem;
}

export class AddItem extends React.Component<IAddTaskProp, IAddTaskState> {
  constructor(props) {
    super(props);

    this.state = {
      task: {}
    };
  }

  addItem = () => {
    this.state.task.type = this.props.type;
    this.props.createEntity(this.state.task);
    this.state.task.title = '';
  };

  updateValue = e => {
    this.state.task.title = e.target.value;
  };

  render() {
    return (
      <Row>
        <Col md="9">
          <form onSubmit={this.addItem}>
            <input type="text" placeholder={'Taskname'} value={this.state.task.title} onChange={this.updateValue} />
            <button type="submit">Add {this.props.type.toString()}!</button>
          </form>
        </Col>
      </Row>
    );
  }
}

const mapStateToProps = (storeState: IRootState) => ({
  updating: storeState.item.updating,
  updateSuccess: storeState.item.updateSuccess
});

const mapDispatchToProps = {
  createEntity
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(AddItem);
