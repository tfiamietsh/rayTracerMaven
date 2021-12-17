package rayTracer;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Element;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;

@SuppressWarnings("serial")
public class Workspace extends JFrame {
	Workspace() {
		//	создаем окно
		frame = new JFrame();
		frame.setTitle("Простой трассировщик лучей");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		
		//	создаем строку меню
		JMenuBar menuBar = new JMenuBar();

		//	создаем менюшку с опциями
		JMenu menuFile = new JMenu("Файл");
		
		//	кнопка опустошения полей сценария и ошибок
		JMenuItem menuFileNew = new JMenuItem("Новый");
		menuFileNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scriptTextArea.setText("");
				errorsTextArea.setText("");
			}
		});
		
		//	кнопка меню открытия файла
		JMenuItem menuFileOpen = new JMenuItem("Открыть");
		menuFileOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser("f:");
				
				int openDialogState = fileChooser.showOpenDialog(null);
				//	если пользователь выбрал файл
				if (openDialogState == JFileChooser.APPROVE_OPTION) {
					File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
					try {
						String line = "", text = "";
						//	записываем содержимое в поле сценария в кодировке UTF-8
						InputStreamReader isr = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
						BufferedReader bufferedReader = new BufferedReader(isr);

						text = bufferedReader.readLine();
						while ((line = bufferedReader.readLine()) != null)
							text = text + "\n" + line;
						
						scriptTextArea.setText(text);
						errorsTextArea.setText("");
						
						bufferedReader.close();
					}
					catch (Exception ex) {
						//	если что-то пошло не так выводим высплывающее окно с ошибкой
						JOptionPane.showMessageDialog(null, ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		
		//	кнопка сохранения файла, работает аналогично кнопке открыть
		JMenuItem menuFileSave = new JMenuItem("Сохранить");
		menuFileSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser("f:");
				
				int saveDialogState = fileChooser.showSaveDialog(null);
				if (saveDialogState == JFileChooser.APPROVE_OPTION) {
					File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
					try {
						OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
						BufferedWriter w = new BufferedWriter(osw);
						
						w.write(scriptTextArea.getText());

						w.flush();
						w.close();
					}
					catch (Exception ex) {
						JOptionPane.showMessageDialog(null, ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		
		//	добавляем кнопки в меню
		menuFile.add(menuFileNew);
		menuFile.add(menuFileOpen);
		menuFile.add(menuFileSave);
		menuFile.add(menuFile);
		
		//	добавляем меню в строку с меню
		menuBar.add(menuFile);
		
		//	добавим еще меню - опции
		JMenu menuOptions = new JMenu("Опции");
		
		//	кнопка, при нажатии на которую открывается текущая директория
		JMenuItem menuOptionsOpenCurrentDir = new JMenuItem("Открыть текущую директорию");
		menuOptionsOpenCurrentDir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String dir = System.getProperty("user.dir");
					Runtime.getRuntime().exec("explorer " + dir);
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		//	кнопка создающая всплывающее меню для задания нового разрешения изображению
		JMenuItem menuOptionsChangeResolution = new JMenuItem("Изменить размер изображения");
		menuOptionsChangeResolution.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//	создаем панельку с 4мя элементами
				JPanel panel = new JPanel(new GridLayout(5, 2));
				JLabel widthLabel = new JLabel("Введите новую ширину изображения");
				JTextField widthField = new JTextField(5);
				widthField.setText(String.valueOf(renderResolution.width));
				JLabel heightLabel = new JLabel("Введите новую высоту изображения");
				JTextField heightField = new JTextField(5);
				heightField.setText(String.valueOf(renderResolution.height));
				//	добавляем элементы в панельку
				panel.add(widthLabel);
				panel.add(widthField);
				panel.add(heightLabel);
				panel.add(heightField);
				//	показываем всплывающее окно с панелькой
				JOptionPane.showMessageDialog(null, panel, "Изменение размеров изображения",
					JOptionPane.PLAIN_MESSAGE);
				
				//	устанавливаем новые размеры, если больше нуля
				int newWidth = Integer.parseInt(widthField.getText());
				int newHeight = Integer.parseInt(heightField.getText());
				if (newWidth > 0 && newHeight > 0) {
					renderResolution.width = newWidth;
					renderResolution.height = newHeight;
				}
			}
		});
		
		//	добавляем кнопки в меню
		menuOptions.add(menuOptionsOpenCurrentDir);
		menuOptions.add(menuOptionsChangeResolution);
		
		//	добавляем меню в строку меню
		menuBar.add(menuOptions);
		
		//	добавим кнопку выполнения сценария
		final JMenuItem menuExecute = new JMenuItem("Выполнить сценарий");
		menuExecute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//	если кнопка антивна,
				if (menuExecute.isEnabled()) {
					//	на время выполнения скрываем прогрессбар и деактивируем кнопку
					viewProgressBar.setVisible(true);
					menuExecute.setEnabled(false);
					
					//	запускаем новый поток с визуализацией
					VizualizationThread thread = new VizualizationThread(
							scriptTextArea.getText(), renderResolution, viewProgressBar, viewImage, errorsTextArea, menuExecute);
					thread.start();
				}
			}
		});
		
		//	этот элемент просто создает пустое пространство между менюшкой опции и кнопкой выполнить
		JLabel menuFiller = new JLabel();
		Dimension menuFillerSize = menuFiller.getMaximumSize();
		menuFillerSize.width = WIDTH - menuFile.getWidth() - menuExecute.getWidth() - 120;
		menuFiller.setPreferredSize(menuFillerSize);
		
		//	добавляем филлер и кнопку в строку меню
		menuBar.add(menuFiller);
		menuBar.add(menuExecute);
		
		//	создаем область вьюшки, где будет показываться изображение
		int viewPanelHeight = (int)(HEIGHT*.7);
		int viewPanelWidth = (int)(viewPanelHeight*(float)(WIDTH)/HEIGHT);
		viewPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		{
			//	настраиваем прогрессбар
			viewProgressBar = new JProgressBar();
			viewProgressBar.setStringPainted(true);
			viewProgressBar.setMinimum(0);
			viewProgressBar.setMaximum(100);
			viewProgressBar.setValue(0);
			viewProgressBar.setPreferredSize(new Dimension((int)(WIDTH*.33), 19));
			viewProgressBar.setVisible(false);
			
			//	создаем боксы для кпаковки прогрессбара строго в центр области вьюшки
			Box b1 = Box.createHorizontalBox();
			b1.add(viewProgressBar);
			b1.add(Box.createRigidArea(new Dimension(WIDTH - viewPanelWidth - 15, 0)));
			Box b2 = Box.createVerticalBox();
			b2.add(b1);
			b2.add(Box.createRigidArea(new Dimension(0, HEIGHT - viewPanelHeight - 75)));
			
			//	слой на котором будет прогрессбар, он перекрывает все остальные элементы
			JPanel glass = (JPanel)frame.getGlassPane();
		    glass.setVisible(true);
		    glass.setLayout(new GridBagLayout());
		    glass.add(b2);
			
			//	загружаем приветственное изображение
		    defaultImage = new JImage("src/main/resources/default.png");
			viewImage = defaultImage;
			viewImage.setPreferredSize(new Dimension(viewPanelWidth, viewPanelHeight));
			viewImage.setVisible(true);
			
			//	добавляем все элементы в панельку вьюшки
			viewPanel.add(viewImage);
			viewPanel.setPreferredSize(new Dimension(viewPanelWidth, viewPanelHeight));
		}
		
		//	создаем панельку для вывода ошибок
		JPanel errorsPanel = new JPanel();
		{
			int infoPanelWidth = viewPanelWidth;
			int infoPanelHeight = HEIGHT - viewPanelHeight - 75;
			
			errorsPanel.setLayout(null);

			//	создаем область текста с ошибками
			errorsTextArea = new JTextArea();
			errorsTextArea.setFont(DEFAULT_FONT);
			errorsTextArea.setEditable(false);
			errorsTextArea.setForeground(new Color(194, 54, 55));
			
			//	прикручиваем скроллбары
			JScrollPane infoScroll = new JScrollPane(errorsTextArea, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			infoScroll.setSize(infoPanelWidth, infoPanelHeight);
			
			//	добавляем элементы в соответствующую панель
			errorsPanel.add(infoScroll);
			errorsPanel.setPreferredSize(new Dimension(infoPanelWidth, infoPanelHeight));
		}
		
		//	поле сценария
		JPanel scriptPanel = new JPanel();
		{
			int scriptPanelWidth = WIDTH - viewPanelWidth - 15;
			int scriptPanelHeight = HEIGHT - 62;
			
			scriptPanel.setLayout(null);
			
			scriptTextArea = new JTextArea();
			scriptTextArea.setFont(DEFAULT_FONT);
			scriptTextArea.setLineWrap(true);
			
			//	сдесь происходит добавление столбца с номером строки
			final JTextArea lines = new JTextArea("1");
			lines.setBackground(new Color(215, 215, 215));
			lines.setFont(DEFAULT_FONT);
			lines.setEditable(false);
			scriptTextArea.getDocument().addDocumentListener(new DocumentListener(){
				public String getText() {
					int caretPosition = scriptTextArea.getDocument().getLength();
					Element root = scriptTextArea.getDocument().getDefaultRootElement();
					String text = "1" + System.getProperty("line.separator");
					for(int i = 2; i < root.getElementIndex(caretPosition) + 2; i++)
						text += i + System.getProperty("line.separator");
					return text;
				}

				public void changedUpdate(DocumentEvent de) {
					lines.setText(getText());
				}
	
				public void insertUpdate(DocumentEvent de) {
					lines.setText(getText());
				}
				
				public void removeUpdate(DocumentEvent de) {
					lines.setText(getText());
				}
			});
			
			//	добавляем вертикальный скроллбар
			JScrollPane scriptScroll = new JScrollPane(scriptTextArea, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			scriptScroll.setSize(scriptPanelWidth, scriptPanelHeight);
			
			//	устанавливаем фичу подсчета строк
			scriptScroll.getViewport().add(scriptTextArea);
			scriptScroll.setRowHeaderView(lines);
			
			scriptPanel.add(scriptScroll);
			scriptPanel.setPreferredSize(new Dimension(scriptPanelWidth, scriptPanelHeight));
		}
		
		//	упаковывем все панельки в боксы, чтобы выглядело красиво
		Box ViewInfoBox = Box.createVerticalBox();
		ViewInfoBox.add(viewPanel);
		
		//	лейбл панели ошибок
		JLabel infoPanelTitle = new JLabel();
		infoPanelTitle.setText("Ошибки");
		Box infoPanelTitleBox = Box.createHorizontalBox();
		infoPanelTitleBox.add(infoPanelTitle);
		
		//	продолжаем добавлять панельки
		ViewInfoBox.add(infoPanelTitleBox);
		ViewInfoBox.add(errorsPanel);
		
		Box ViewInfoScriptBox = Box.createHorizontalBox();
		ViewInfoScriptBox.add(ViewInfoBox);
		ViewInfoScriptBox.add(scriptPanel);
		
		//	настраиваем параметры окна
		frame.setJMenuBar(menuBar);
		frame.add(ViewInfoScriptBox);
		frame.pack();
		frame.setSize(WIDTH, HEIGHT);
		frame.setVisible(true);
		
		//	обработка глобального нажатия Esc с закрытием приложения
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
			.addKeyEventDispatcher(new KeyEventDispatcher() {
				public boolean dispatchKeyEvent(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
						frame.dispose();
					return false;
				}
			}
		);
	}
	
	JTextArea scriptTextArea;
	JTextArea errorsTextArea;
	JPanel viewPanel;
	JProgressBar viewProgressBar;
	JImage viewImage;
	JFrame frame;
	JImage defaultImage, renderedImage = null;
	Dimension renderResolution = new Dimension(1920, 1080);
	final int WIDTH = 1600;
	final int HEIGHT = 900;
	final Font DEFAULT_FONT = new Font("Segoe UI", Font.PLAIN, 20);
}