/*** Produzido por André Luiz Coutinho
 * 
 */


package com.coutigames.main;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class Game extends Canvas implements Runnable,MouseMotionListener,MouseListener, KeyListener{

	public static final int WIDTH = 488, HEIGHT = 330;
	public static final int SCALE = 2;
	
	public static final int FPS = 1000/60;
	
	public Tabuleiro tabuleiro;
	public static JFrame frame;
	
	public static int points = 0;	 
	
	public BufferedImage image = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
	
	public static boolean selected = false;
	public static int previousx = 0,previousy = 0;
	public static int nextx = -1,nexty = -1;
	public static Ui ui;
	
	// Variável para controlar o tempo do último clique do mouse
	private long lastClickTime = System.currentTimeMillis();
	// Controla se deve piscar e qual peça
	private boolean shouldBlink = false;
	private int blinkX = -1, blinkY = -1;
	private boolean blinkState = false;
	private long lastBlinkToggle = 0;
	
	public Game() {
		this.setPreferredSize(new Dimension(WIDTH*SCALE,HEIGHT*SCALE));
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addKeyListener(this);
		tabuleiro = new Tabuleiro();
		ui = new Ui();
	}
	
	public static void main(String[] args) {
		Game.frame = new JFrame("Candy_Crush_Couti");
		Game game = new Game();
		frame.add(game);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();		
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);		
		new Thread(game).start();
	}
	
	public void update() {
		
		tabuleiro.update();
		ui.tick();
		
		if(Game.previousx < 0 || Game.previousx >= Tabuleiro.GRID_SIZE * Tabuleiro.WIDTH
			|| Game.previousy < 0 || Game.previousy >= Tabuleiro.GRID_SIZE * Tabuleiro.HEIGHT	
				) {
			Game.nextx = -1;
			Game.nexty = -1;
			Game.selected = false;
		}		
		if(Game.selected && (Game.nextx != -1 && Game.nexty != -1)) {
			
			if(Game.nextx < 0 || Game.nextx >= Tabuleiro.GRID_SIZE * Tabuleiro.WIDTH
					|| Game.nexty < 0 || Game.nexty >= Tabuleiro.GRID_SIZE * Tabuleiro.HEIGHT	
						) {
					Game.nextx = -1;
					Game.nexty = -1;
					Game.selected = false;
				}
			int posx1 = Game.previousx/Tabuleiro.GRID_SIZE;
			int posy1 = Game.previousy/Tabuleiro.GRID_SIZE;
			
			int pos2x = Game.nextx/Tabuleiro.GRID_SIZE;
			int pos2y = Game.nexty/Tabuleiro.GRID_SIZE;
			
			// Verifica se o movimento é válido (resultará em um combo)
			if(Tabuleiro.isValidMove(posx1, posy1, pos2x, pos2y)) {
				// Faz a troca das peças
				int val1 = Tabuleiro.TABULEIRO[pos2x][pos2y];
				int val2 = Tabuleiro.TABULEIRO[posx1][posy1];
				Tabuleiro.TABULEIRO[posx1][posy1] = val1;
				Tabuleiro.TABULEIRO[pos2x][pos2y] = val2;
				Game.nextx = -1;
				Game.nexty = -1;
				Game.selected = false;
				System.out.println("Movimento válido - combo criado!");
			} else {
				// Movimento inválido - não faz a troca
				Game.nextx = -1;
				Game.nexty = -1;
				Game.selected = false;
				System.out.println("Movimento inválido - não resulta em combo!");
			}
			}else {
				
				//System.out.println("nao pode mover");
			}
			
		}
	
	
	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		
		Graphics g = image.getGraphics();
		//		
		g.setColor(new Color(0,255,255));		
		g.fill3DRect(0, 0, WIDTH, HEIGHT, isCursorSet());
		//g.fill3DRect(nextx, nexty, WIDTH, HEIGHT, selected);
		//g.fillRect(0, 0, WIDTH, HEIGHT);
		tabuleiro.render(g, blinkX, blinkY, blinkState);
		//
		
		g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0,WIDTH*SCALE,HEIGHT*SCALE,null);
		
		
		ui.render(g);
		bs.show();
	}

	@Override
	public void run() {
		while(true) {
			// Verifica inatividade
			long now = System.currentTimeMillis();
			if (now - lastClickTime > 10000 && !shouldBlink) {
				// Solicita ao tabuleiro uma peça que pode completar combo
				int[] pos = tabuleiro.findHintCombo();
				if (pos != null) {
					blinkX = pos[0];
					blinkY = pos[1];
					shouldBlink = true;
				}
			}
			// Alterna o estado de piscar a cada 400ms
			if (shouldBlink && now - lastBlinkToggle > 400) {
				blinkState = !blinkState;
				lastBlinkToggle = now;
			}
			update();
			render();
			try {
				Thread.sleep(FPS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(Tabuleiro.isGameOver) {
			return;
		}
		// Reseta o temporizador de inatividade e piscar
		lastClickTime = System.currentTimeMillis();
		shouldBlink = false;
		blinkX = -1;
		blinkY = -1;
		// TODO Auto-generated method stub		
		if(Game.selected == false) {			
			Game.selected = true;
			Sound.Movimento.play();
			Game.previousx = e.getX()/2 - 24;
			Game.previousy = e.getY()/2 - 24;			
		}else {
			Game.nextx = e.getX()/2 - 24;
			Game.nexty = e.getY()/2 - 24;
		}
	}
		// Método para lidar com teclas no Game
	public void keyPressed(KeyEvent e) {
		    int key = e.getKeyCode();

		    // Reinicia o jogo ao pressionar R
		 if (tabuleiro.isGameOver && key == KeyEvent.VK_R) {
		        tabuleiro.restartGame();
		    }
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// Bloqueia o mouse se o jogo estiver em Game Over
		if(Tabuleiro.isGameOver) {
			return;
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// Bloqueia o mouse se o jogo estiver em Game Over
		if(Tabuleiro.isGameOver) {
			return;
		}
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// Bloqueia o mouse se o jogo estiver em Game Over
		if(Tabuleiro.isGameOver) {
			return;
		}
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// Bloqueia o mouse se o jogo estiver em Game Over
		if(Tabuleiro.isGameOver) {
			return;
		}
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	
	}
