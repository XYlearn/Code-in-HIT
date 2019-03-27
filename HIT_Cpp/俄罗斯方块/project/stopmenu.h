#ifndef STOPMENU_H
#define STOPMENU_H
#include <QGraphicsView>
#include <QPushButton>
#include <QWidget>
#include <QObject>

class StopMenu : public QWidget
{
    Q_OBJECT

public:
    StopMenu(QWidget *parent = nullptr);

protected:
    ~StopMenu() {}
private:
    QPushButton *returnButton;
    QPushButton *restartButton;
    QPushButton *backButton;

signals:
    void returnToMain();
    void restartGame();
    void backToGame();

};

#endif // STOPMENU_H
