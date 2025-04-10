package com.customs;


import javax.swing.*;
import java.awt.*;

public class LoadingBar extends JPanel {
	//ȸ�� ���� ����(0������)
    private int angle = 0;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //Graphics2D�� �ε巯�� �׸���
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //��� ���
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        //�� ũ��, ��ġ(�г� �߾�)
        int diameter = 100;
        int x = (getWidth() - diameter) / 2;
        int y = (getHeight() - diameter) / 2;
        
        //ȸ�� (�ε� �ٱ� ��) �׸���
        g2d.setColor(Color.GRAY);
        g2d.setStroke(new BasicStroke(10));
        g2d.drawArc(x, y, diameter, diameter, 0, -angle);

        //�� �߾� ������� ���� ���� ���·� ���̰� 
        g2d.setColor(Color.WHITE);
        g2d.fillOval(x + 10, y + 10, diameter - 20, diameter - 20);

        //ȸ�� ���� ���� -> ȸ�� �ִϸ��̼�
        angle += 2;
        if (angle >= 360) {
            angle = 0;
        }
        //�ణ ���� �� �ٽ� �ִϸ��̼� �ݺ�
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //�ٽñ׸���
        repaint();
    }
}
