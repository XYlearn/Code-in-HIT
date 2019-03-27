#ifndef TERIXITEM_H
#define TERIXITEM_H

#include <QGraphicsItemGroup>
#include <QColor>
#include <QPainter>
#include <QList>
#include <QRect>

class TerisSingleItem : public QGraphicsObject
{
public:
    TerisSingleItem(QGraphicsObject *parent = nullptr):QGraphicsObject(parent) {}
    ~TerisSingleItem() {}

    void setColor(const QColor &color) {this->color = color;}
    QPainterPath shape() const;
    QRectF boundingRect() const
    {
        qreal penWidth= 2;
        return QRectF(-15-penWidth/2, -15-penWidth/2, 30 + penWidth, 30 + penWidth);
    }
    void paint(QPainter *painter, const QStyleOptionGraphicsItem *option, QWidget *widget);
    bool isBoomer() {return boomer;}
private:
    QColor color;
    bool boomer;
};

class TerisItem : public QObject, public QGraphicsItemGroup
{
    Q_OBJECT
public:
    enum Shape{IShape, JShape, LShape, TShape, SqShap, ZShap, SShap, RandShape, DotShap};
    TerisItem();
    ~TerisItem();
    QRectF boundingRect() const {
        qreal pen = 2;
        return QRectF(-60 - pen/2, -60 - pen/2, 120+pen, 120 + pen);
    }

public:
    void newItem(const QPoint &point, Shape shape = RandShape);
    bool isColliding();
    bool moveDown();
    void clearItem(bool destroy);
    Shape getShape() {return shape;}

protected:
    void keyPressEvent(QKeyEvent *event);

private:
    Shape shape;
    void moveLeft();
    void moveRight();
    void rotate(bool);
    QTransform tempTransform;

public slots:


signals:
    void gameOver();
};

#endif // TERIXITEM_H
