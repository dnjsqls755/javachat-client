package view.frame;

import app.Application;
import domain.User;
import dto.request.LoginRequest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.RoundRectangle2D;

public class LoginFrame extends JFrame implements ActionListener {

    private static final Color SAND = new Color(244, 236, 221);
    private static final Color KAKAO_YELLOW = new Color(255, 232, 18);
    private static final Color DEEP_BROWN = new Color(52, 40, 32);
    private static final Font DEFAULT_FONT = new Font("맑은 고딕", Font.PLAIN, 13);

    private final LobbyFrame lobbyFrame;
    private final JTextField idField = new JTextField();
    private final JPasswordField pwField = new JPasswordField();
    private final JButton loginButton = new JButton("로그인");
    private final JButton qrButton = new JButton("QR코드 로그인");
    private final JButton joinLink = new JButton("회원가입");
    private final JComboBox<String> cityComboBox;
    private final JLabel weatherIconLabel = new JLabel();
    private final JLabel temperatureLabel = new JLabel("");
    
    private static final String API_KEY = "1e9136088e03c34b29269b0e130d17a1"; // OpenWeatherMap API 키를 입력하세요
    private static final String[] CITIES = {
        "Seoul",       // 서울특별시
        "Busan",       // 부산광역시
        "Daegu",       // 대구광역시
        "Incheon",     // 인천광역시
        "Gwangju",     // 광주광역시
        "Daejeon",     // 대전광역시
        "Ulsan",       // 울산광역시
        "Sejong",      // 세종특별자치시
        "Gyeonggi-do", // 경기도
        "Gangwon-do",  // 강원도
        "Chungcheongbuk-do", // 충청북도
        "Chungcheongnam-do", // 충청남도
        "Jeollabuk-do",      // 전라북도
        "Jeollanam-do",      // 전라남도
        "Gyeongsangbuk-do",  // 경상북도
        "Gyeongsangnam-do",  // 경상남도
        "Jeju-do"            // 제주특별자치도
    };

    private boolean loggingIn = false;
public LoginFrame(LobbyFrame lobbyFrame) {
        this.lobbyFrame = lobbyFrame;
        Application.loginFrame = this;
        
        // 콤보박스 초기화
        cityComboBox = new JComboBox<>(CITIES);
        cityComboBox.setSelectedIndex(0);
        
        setTitle("Chat - Login");
        setSize(410, 700);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(SAND);

        JPanel canvas = new JPanel();
        canvas.setLayout(new BoxLayout(canvas, BoxLayout.Y_AXIS));
        canvas.setBackground(KAKAO_YELLOW);
        canvas.setBorder(BorderFactory.createEmptyBorder(26, 34, 30, 34));

        canvas.add(buildTopBar());
        canvas.add(Box.createVerticalStrut(18));
        canvas.add(buildLogo());
        canvas.add(Box.createVerticalStrut(20));
        canvas.add(buildWeatherPanel());
        canvas.add(Box.createVerticalStrut(20));
        canvas.add(buildInputPanel());
        canvas.add(Box.createVerticalStrut(16));
        canvas.add(buildLoginButtons());
        canvas.add(Box.createVerticalStrut(14));
        canvas.add(buildDivider("또는"));
        canvas.add(Box.createVerticalStrut(14));
        canvas.add(buildQrButton());
        canvas.add(Box.createVerticalStrut(16));
        canvas.add(buildAutoLogin());
        canvas.add(Box.createVerticalGlue());
        canvas.add(buildFooterLinks());

        root.add(canvas, BorderLayout.CENTER);
        setContentPane(root);
        setVisible(true);
    }

    private JComponent buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        actions.setOpaque(false);
        actions.add(createIconButton("\u2699", "설정"));
        actions.add(createIconButton("\u2013", "최소화"));
        bar.add(actions, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildLogo() {
        JPanel bubble = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Shape shape = new RoundRectangle2D.Double(0, 0, getWidth(), getHeight() - 12, 32, 32);
                g2.setColor(DEEP_BROWN);
                g2.fill(shape);
                Polygon tail = new Polygon();
                tail.addPoint(getWidth() / 2 - 10, getHeight() - 12);
                tail.addPoint(getWidth() / 2 + 10, getHeight() - 12);
                tail.addPoint(getWidth() / 2, getHeight());
                g2.fillPolygon(tail);
                g2.dispose();
            }
        };
        bubble.setPreferredSize(new Dimension(140, 78));
        bubble.setMaximumSize(new Dimension(140, 78));
        bubble.setOpaque(false);

        JLabel talk = new JLabel("TALK", SwingConstants.CENTER);
        talk.setForeground(KAKAO_YELLOW);
        talk.setFont(new Font("Arial Black", Font.BOLD, 22));
        bubble.add(talk, BorderLayout.CENTER);

        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.add(bubble);
        return wrapper;
    }

    private JPanel buildInputPanel() {
        JPanel inputs = new JPanel();
        inputs.setOpaque(false);
        inputs.setLayout(new BoxLayout(inputs, BoxLayout.Y_AXIS));

        idField.setPreferredSize(new Dimension(260, 44));
        idField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        styleField(idField);
        idField.setToolTipText("아이디 또는 이메일을 입력하세요");
        idField.setFont(DEFAULT_FONT);

        pwField.setPreferredSize(new Dimension(260, 44));
        pwField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        styleField(pwField);
        pwField.setToolTipText("비밀번호를 입력하세요");
        pwField.setFont(DEFAULT_FONT);

        inputs.add(idField);
        inputs.add(Box.createVerticalStrut(8));
        inputs.add(pwField);
        return inputs;
    }

    private JPanel buildLoginButtons() {
        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));

        stylePrimaryButton(loginButton);
        loginButton.addActionListener(this);

        buttons.add(loginButton);
        return buttons;
    }

    private JComponent buildDivider(String text) {
        JPanel divider = new JPanel();
        divider.setOpaque(false);
        divider.setLayout(new BoxLayout(divider, BoxLayout.X_AXIS));

        JSeparator left = new JSeparator();
        JSeparator right = new JSeparator();
        JLabel label = new JLabel(text);
        label.setForeground(new Color(90, 90, 90));
        label.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

        divider.add(left);
        divider.add(label);
        divider.add(right);
        return divider;
    }

    private JPanel buildQrButton() {
        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.X_AXIS));
        styleSecondaryButton(qrButton);
        qrButton.setHorizontalAlignment(SwingConstants.CENTER);
        qrButton.addActionListener(this);
        wrapper.add(qrButton);
        return wrapper;
    }

    private JPanel buildAutoLogin() {
        JPanel auto = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        auto.setOpaque(false);
        JCheckBox check = new JCheckBox("자동 로그인");
        check.setOpaque(false);
        check.setSelected(false);
        check.setForeground(Color.DARK_GRAY);
        JLabel info = new JLabel("\u2139");
        info.setForeground(new Color(90, 90, 90));
        auto.add(check);
        auto.add(info);
        return auto;
    }

    private JPanel buildFooterLinks() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        footer.setOpaque(false);

        joinLink.setFocusPainted(false);
        joinLink.setBorderPainted(false);
        joinLink.setContentAreaFilled(false);
        joinLink.setForeground(new Color(60, 60, 60));
        joinLink.setFont(DEFAULT_FONT.deriveFont(Font.BOLD, 12f));
        joinLink.addActionListener(this);

        JLabel findAccount = new JLabel("<html><u>카카오계정 찾기</u></html>");
        findAccount.setForeground(new Color(60, 60, 60));
        JLabel divider = new JLabel("|");
        divider.setForeground(new Color(120, 120, 120));
        JLabel resetPwd = new JLabel("<html><u>비밀번호 재설정</u></html>");
        resetPwd.setForeground(new Color(60, 60, 60));

        footer.add(findAccount);
        footer.add(divider);
        footer.add(resetPwd);
        footer.add(joinLink);
        return footer;
    }
    
    private JPanel buildWeatherPanel() {
        JPanel weatherPanel = new JPanel();
        weatherPanel.setOpaque(false);
        weatherPanel.setLayout(new BoxLayout(weatherPanel, BoxLayout.Y_AXIS));
        
        // 도시 선택 콤보박스
        JPanel cityPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        cityPanel.setOpaque(false);
        JLabel cityLabel = new JLabel("도시: ");
        cityLabel.setFont(DEFAULT_FONT.deriveFont(Font.BOLD));
        cityLabel.setForeground(DEEP_BROWN);
        
        cityComboBox.setFont(DEFAULT_FONT);
        cityComboBox.setPreferredSize(new Dimension(180, 30));
        cityComboBox.setBackground(Color.WHITE);
        cityComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    updateWeather();
                }
            }
        });
        
        cityPanel.add(cityLabel);
        cityPanel.add(cityComboBox);
        
        // 날씨 정보 표시 패널
        JPanel weatherInfoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        weatherInfoPanel.setOpaque(false);
        
        weatherIconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        temperatureLabel.setFont(DEFAULT_FONT.deriveFont(Font.BOLD, 16f));
        temperatureLabel.setForeground(DEEP_BROWN);
        
        weatherInfoPanel.add(weatherIconLabel);
        weatherInfoPanel.add(temperatureLabel);
        
        weatherPanel.add(cityPanel);
        weatherPanel.add(Box.createVerticalStrut(10));
        weatherPanel.add(weatherInfoPanel);
        
        // 초기 날씨 정보 로드
        updateWeather();
        
        return weatherPanel;
    }
    
    private void updateWeather() {
        String selectedCity = (String) cityComboBox.getSelectedItem();
        if (selectedCity == null) return;
        
        // 백그라운드에서 날씨 정보 가져오기
        new Thread(() -> {
            String[] weatherInfo = WeatherFetcher.getWeatherInfo(selectedCity, API_KEY);
            String weatherCondition = weatherInfo[0];
            String temperature = weatherInfo[1];
            
            // UI 업데이트는 EDT에서 실행
            SwingUtilities.invokeLater(() -> {
                String icon = getWeatherIcon(weatherCondition);
                weatherIconLabel.setText(icon);
                if (!temperature.equals("N/A")) {
                    temperatureLabel.setText(temperature + "°C");
                } else {
                    temperatureLabel.setText("");
                }
            });
        }).start();
    }
    
    private String getWeatherIcon(String weatherCondition) {
        switch (weatherCondition.toLowerCase()) {
            case "clear":
                return "☀️";  // 맑음
            case "clouds":
                return "☁️";  // 흐림
            case "rain":
            case "drizzle":
                return "🌧️";  // 비
            case "snow":
                return "❄️";  // 눈
            case "thunderstorm":
                return "⛈️";  // 뇌우
            case "mist":
            case "fog":
            case "haze":
                return "🌫️";  // 안개
            default:
                return "🌤️";  // 기본
        }
    }

    private JButton createIconButton(String text, String tooltip) {
        JButton btn = new JButton(text);
        btn.setOpaque(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setToolTipText(tooltip);
        btn.setForeground(new Color(70, 70, 70));
        return btn;
    }

    private void styleField(JTextField field) {
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        field.setFont(DEFAULT_FONT);
        field.setBackground(Color.WHITE);
    }

    private void stylePrimaryButton(JButton btn) {
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setPreferredSize(new Dimension(260, 46));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(245, 245, 245));
        btn.setForeground(Color.DARK_GRAY);
        btn.setBorder(BorderFactory.createLineBorder(new Color(215, 215, 215)));
    }

    private void styleSecondaryButton(JButton btn) {
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setPreferredSize(new Dimension(260, 46));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btn.setFocusPainted(false);
        btn.setBackground(Color.WHITE);
        btn.setForeground(Color.DARK_GRAY);
        btn.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210)));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            String id = idField.getText().trim();
            String pw = new String(pwField.getPassword()).trim();

            if (loggingIn) {
                return;
            }
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "???? ??????.", "??? ??", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (pw.isEmpty()) {
                JOptionPane.showMessageDialog(this, "????? ??????.", "??? ??", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Application.me = new User(id, "");
            Application.sender.sendMessage(new LoginRequest(id, pw));
            loggingIn = true;
            loginButton.setEnabled(false);
            loginButton.setText("??? ?...");
        }

        if (e.getSource() == qrButton) {
            JOptionPane.showMessageDialog(this, "QR?? ???? ??????.", "??", JOptionPane.INFORMATION_MESSAGE);
        }

        if (e.getSource() == joinLink) {
            new JoinFrame();
        }
    }



    public void handleLoginFailure(String message) {
        loggingIn = false;
        loginButton.setEnabled(true);
        loginButton.setText("???");
        JOptionPane.showMessageDialog(this, message, "??? ??", JOptionPane.ERROR_MESSAGE);
    }

}