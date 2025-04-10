package com.customs;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Round {
    
    //RoundTextField
    public static class RoundTextField extends JTextField {
        private int arcWidth = 6;
        private int arcHeight = 6;

        public RoundTextField(int size) {
            super(size);
            setOpaque(false);	//배경 투명
            setBorder(new EmptyBorder(5, 10, 5, 10));	//내부 여백
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);	//배경색
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arcWidth, arcHeight);	//둥근 사각형배경
            super.paintComponent(g);	//텍스트 그리기
            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.BLACK);	//테두리색
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arcWidth, arcHeight);	//테두리 그리기
            g2.dispose();
        }
    }
    
    //둥근 모서리 비밀번호 필드
    public static class RoundPasswordField extends JPasswordField {
        private int arcWidth = 6;
        private int arcHeight = 6;

        public RoundPasswordField(int size) {
            super(size);
            setOpaque(false);
            setBorder(new EmptyBorder(5, 10, 5, 10));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arcWidth, arcHeight);
            super.paintComponent(g);
            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.BLACK);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arcWidth, arcHeight);
            g2.dispose();
        }
    }

    //둥근 모서리 버튼
    public static class RoundButton extends JButton {
        private int arcWidth = 6;
        private int arcHeight = 6;

        public RoundButton(String text) {
            super(text);
            setContentAreaFilled(false);	//배경 직접 그림
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arcWidth, arcHeight); //배경 그리기
            super.paintComponent(g);	//텍스트 및 내부 요소 그리기
            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.BLACK);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arcWidth, arcHeight);//테두리
            g2.dispose();
        }
    }
    
    // 둥근 배경 가진 패널
    public static class RoundPanel extends JPanel {
        private int arcWidth = 30;
        private int arcHeight = 30;

        public RoundPanel(Color bgColor) {
            setBackground(bgColor);
            setOpaque(false);
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));	//세로 정렬
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arcWidth, arcHeight);	//둥근 배경
            g2.dispose();
            super.paintComponent(g);	//자식 컴포넌트 그리기
        }
    }
}
