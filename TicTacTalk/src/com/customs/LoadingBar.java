package com.customs;


import javax.swing.*;
import java.awt.*;

public class LoadingBar extends JPanel {
	//회전 각도 변수(0도부터)
    private int angle = 0;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //Graphics2D로 부드러운 그리기
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //배경 흰색
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        //원 크기, 위치(패널 중앙)
        int diameter = 100;
        int x = (getWidth() - diameter) / 2;
        int y = (getHeight() - diameter) / 2;
        
        //회전 (로딩 바깥 원) 그리기
        g2d.setColor(Color.GRAY);
        g2d.setStroke(new BasicStroke(10));
        g2d.drawArc(x, y, diameter, diameter, 0, -angle);

        //원 중앙 흰색으로 덮어 도넛 형태로 보이게 
        g2d.setColor(Color.WHITE);
        g2d.fillOval(x + 10, y + 10, diameter - 20, diameter - 20);

        //회전 각도 증가 -> 회전 애니메이션
        angle += 2;
        if (angle >= 360) {
            angle = 0;
        }
        //약간 지연 후 다시 애니메이션 반복
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //다시그리기
        repaint();
    }
}
