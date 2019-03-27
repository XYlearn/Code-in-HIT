#include "terisitem.h"

#include <QList>
#include <QGraphicsItem>
#include <QGraphicsItemGroup>
#include <QPainter>
#include <QPainterPath>
#include <QTransform>
#include <QSet>
#include <cstdlib>
#include <ctime>

void TerisSingleItem::paint(QPainter *painter, const QStyleOptionGraphicsItem *option, QWidget *widget)
{
    qreal pen = 2;
    painter->drawPixmap(-15, -15, 30, 30, QPixmap());
    painter->setBrush(color);
    painter->drawRect(-15, -15, 30, 30);
    QColor tempColor = color;
    tempColor.setAlpha(40);
    painter->setPen(tempColor);
    painter->drawRect(-15+pen/2, -15+pen/2, 30-pen, 30-pen);
}

QPainterPath TerisSingleItem::shape() const
{
    QPainterPath path;
    path.addRect(-14.5, -14.5, 29, 29);
    return path;
}

TerisItem::TerisItem() : tempTransform(transform())
{
    setFlags(QGraphicsItem::ItemIsFocusable);
    srand(time(nullptr));
}

TerisItem::~TerisItem()
{

}

void TerisItem::newItem(const QPoint &pos, Shape shape)
{
    QColor colors[] = {
        QColor(200, 0, 0), QColor(0, 200, 0), QColor(0, 0, 200), QColor(100, 100, 0),
        QColor(100, 0, 100), QColor(0, 100, 100), QColor(100, 100, 100)
    };
    int id = shape==RandShape? rand()%7 : int(shape);
    QColor color = colors[id];
    QList<TerisSingleItem *> lst;

    setTransform(tempTransform);
    for(int i=0; i < 4; i++)
    {
        TerisSingleItem *item = new TerisSingleItem();
        item->setColor(color);
        addToGroup(item);
        lst.append(item);
    }

    switch (Shape(id)) {

    case IShape:
        /* |    |
         * |****|
         * |    |
         * |    |
         */
        lst.at(0)->setPos(-45, -15);
        lst.at(1)->setPos(-15, -15);
        lst.at(2)->setPos(15, -15);
        lst.at(3)->setPos(45, -15);
        break;

    case JShape:
       /* |*   |
        * |*** |
        * |    |
        * |    |
        */
        lst.at(0)->setPos(-45, -45);
        lst.at(1)->setPos(-45, -15);
        lst.at(2)->setPos(-15, -15);
        lst.at(3)->setPos(15, -15);
        break;

    case LShape:
        /* |  * |
         * |*** |
         * |    |
         * |    |
         */
        lst.at(0)->setPos(15, -45);
        lst.at(1)->setPos(-45, -15);
        lst.at(2)->setPos(-15, -15);
        lst.at(3)->setPos(15, -15);
        break;

    case TShape:
        /* | *  |
         * |*** |
         * |    |
         * |    |
         */
        lst.at(0)->setPos(-15, -45);
        lst.at(1)->setPos(-45, -15);
        lst.at(2)->setPos(-15, -15);
        lst.at(3)->setPos(15, -15);
        break;

    case SqShap:
        /* | ** |
         * | ** |
         * |    |
         * |    |
         */
        lst.at(0)->setPos(-15, -45);
        lst.at(1)->setPos(15, -45);
        lst.at(2)->setPos(-15, -15);
        lst.at(3)->setPos(15, -15);
        break;

    case ZShap:
        /* |**  |
         * | ** |
         * |    |
         * |    |
         */
        lst.at(0)->setPos(-45, -15);
        lst.at(1)->setPos(-15, -15);
        lst.at(2)->setPos(15, -15);
        lst.at(3)->setPos(45, -15);
        break;

    case SShap:
        /* | ** |
         * |**  |
         * |    |
         * |    |
         */
        lst.at(0)->setPos(-15, -45);
        lst.at(1)->setPos(15, -45);
        lst.at(2)->setPos(-45, -15);
        lst.at(3)->setPos(-15, -15);
        break;

    default:
        break;
    }
    this->shape = Shape(id);
    setPos(pos);
    if(isColliding())
    {
        emit gameOver();
    }
}

void TerisItem::keyPressEvent(QKeyEvent *event)
{
    this->setFocus();
    switch (event->key()) {
    case Qt::Key_Down:
        moveDown();
        break;
    case Qt::Key_Left:
        moveLeft();
        break;
    case Qt::Key_Right:
        moveRight();
        break;
    case Qt::Key_Up:
        rotate(true);
        if(isColliding())
            rotate(false);
        break;
    default:
        break;
    }
}

void TerisItem::rotate(bool clockwise)
{
    int angle = clockwise ? 90 : -90;
    Shape shape = this->shape;
    if(shape == SqShap)
        return;
    else if(shape == IShape)
    {
        setTransformOriginPoint(this->x()+boundingRect().center().x(), this->y()+boundingRect().center().y());
        tempTransform.rotate(angle);
    }
    else
    {
        setTransformOriginPoint(this->x()+boundingRect().center().x()-15, this->y()+boundingRect().center().y()-15);
        tempTransform.rotate(angle);
    }
    setTransform(tempTransform);
}

bool TerisItem::moveDown()
{
    moveBy(0, 30);
    if(isColliding())
    {
        moveBy(0, -30);
        return false;
    }
    return true;
}

void TerisItem::moveLeft()
{
    moveBy(-30, 0);
    if(isColliding())
        moveBy(30, 0);
}

void TerisItem::moveRight()
{
    moveBy(30, 0);
    if(isColliding())
        moveBy(-30, 0);
}

bool TerisItem::isColliding()
{
    QList<QGraphicsItem *> itemList = childItems();
    for(auto item : itemList)
    {
        if(item->collidingItems().count() > 1)
            return true;
    }
    return false;
}

void TerisItem::clearItem(bool destroy)
{
    for(QGraphicsItem *singleItem : childItems())
    {
        removeFromGroup(singleItem);
        if(destroy)
        {
            TerisSingleItem *itemToDelete = (TerisSingleItem *)singleItem;
            itemToDelete->deleteLater();
        }
    }
}
