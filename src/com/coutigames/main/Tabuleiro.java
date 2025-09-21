package com.coutigames.main;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.List;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

public class Tabuleiro {

	public static BufferedImage spritesheet;
	
	public static final int WIDTH = 11,HEIGHT = 7;
	public static int[][] TABULEIRO;
	
	// Variáveis globais no Game
	public static boolean isGameOver = false; // Indica se o jogo está no estado de Game Over
	private static long startTime = System.currentTimeMillis(); // Marca o início do cronômetro
	private Font gameOverFont = new Font("Arial", Font.BOLD, 50); // Fonte para Game Over
	private static final Font restartFont = new Font("Arial", Font.PLAIN, 20); // Fonte para instruções
	
	public static int GRID_SIZE = 40;
	
	public static int DOCE_0 = 0,DOCE_1 = 1, DOCE_2 = 2;
	public static int DOCE_3 = 3,DOCE_4 = 4, DOCE_5 = 5;
	public static int DOCE_6 = 6,DOCE_7 = 7, DOCE_8 = 8;
	public static int DOCE_9 = 9,DOCE_10 = 10, DOCE_11 = 11;
	
	public BufferedImage DOCE_0_SPRITE = Tabuleiro.getSprite(1299,188,117,117);
	public BufferedImage DOCE_1_SPRITE = Tabuleiro.getSprite(1310,338,95,127);
	public BufferedImage DOCE_2_SPRITE = Tabuleiro.getSprite(971,281,106,116);
	
	public BufferedImage DOCE_3_SPRITE = Tabuleiro.getSprite(1118,179,134,136);
	public BufferedImage DOCE_4_SPRITE = Tabuleiro.getSprite(808,15,129,136);
	public BufferedImage DOCE_5_SPRITE = Tabuleiro.getSprite(629,178,151,137);
	
	public BufferedImage DOCE_6_SPRITE = Tabuleiro.getSprite(1028,1535,125,118);
	public BufferedImage DOCE_7_SPRITE = Tabuleiro.getSprite(859,1528,130,134);
	public BufferedImage DOCE_8_SPRITE = Tabuleiro.getSprite(984,147,77,115);
	
	
	public BufferedImage DOCE_9_SPRITE = Tabuleiro.getSprite(1455,797,117,121);
	public BufferedImage DOCE_10_SPRITE = Tabuleiro.getSprite(1769,643,114,117);
	public BufferedImage DOCE_11_SPRITE = Tabuleiro.getSprite(629,9,153,139);	
	
	
	public Tabuleiro() {
		TABULEIRO = new int[WIDTH][HEIGHT];
		boolean temCombo = false;
		do {
			for(int x = 0; x < WIDTH; x++) {
				for(int y = 0; y < HEIGHT; y++) {
					TABULEIRO[x][y] = new Random().nextInt(12);
				}
			}
			temCombo = this.findHintCombo() != null;
		} while (!temCombo);
	}
	
	public static BufferedImage getSprite(int x,int y,int width,int height) {
		if(spritesheet == null) {
			try {
				spritesheet = ImageIO.read(Tabuleiro.class.getResource("/spritesheet.png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return spritesheet.getSubimage(x, y, width, height);
	}
	
	// Método principal de atualização
	public void update() {
	    if (isGameOver) {
	        return; // Pausa o jogo se estiver em Game Over
	    }

	    // Verifica o tempo restante
	    long elapsedTime = (System.currentTimeMillis() - startTime) / 2000; // Tempo em segundos
	    if (elapsedTime > 30) {
	        gameOver();
	        return;
	    }

	    // Permite múltiplos combos por jogada (cascata)
	    boolean houveCombo;
	    do {
	        houveCombo = false;
	        if (checkHorizontalCombos()) {
	            houveCombo = true;
	        }
	        if (checkVerticalCombos()) {
	            houveCombo = true;
	        }
	    } while (houveCombo);
    }

	// Método para verificar combos horizontais
	private boolean checkHorizontalCombos() {
	    boolean encontrouCombo = false;
	    
	    for (int yy = 0; yy < HEIGHT; yy++) {
	        ArrayList<Candy> combos = new ArrayList<Candy>();
	        int corAnterior = -1;

	        for (int xx = 0; xx < WIDTH; xx++) {
	            int cor = TABULEIRO[xx][yy];
	            if (cor == corAnterior) {
	                combos.add(new Candy(xx, yy, cor));
	            } else {
	                if (combos.size() >= 3) {
	                    Sound.Combo_1.play();
	                    processCombo(new ArrayList<Candy>(combos));
	                    encontrouCombo = true;
	                }
	                combos.clear();
	                combos.add(new Candy(xx, yy, cor));
	            }
	            corAnterior = cor;
	        }

	        // Verifica combo no final da linha
	        if (combos.size() >= 3) {
	            Sound.Combo_1.play();
	            processCombo(new ArrayList<Candy>(combos));
	            encontrouCombo = true;
	        }
	    }

	    // Garante que sempre haja pelo menos um combo possível após atualizar o tabuleiro
	    boolean temCombo = this.findHintCombo() != null;
	    while (!temCombo) {
	        for (int x = 0; x < WIDTH; x++) {
	            for (int y = 0; y < HEIGHT; y++) {
	                TABULEIRO[x][y] = new Random().nextInt(12); // 12 cores possíveis
	            }
	        }
	        temCombo = this.findHintCombo() != null;
	    }

	    return encontrouCombo;
	}


	// Método para verificar combos verticais
	private boolean checkVerticalCombos() {
	    boolean encontrouCombo = false;
	    for (int xx = 0; xx < WIDTH; xx++) {
	        ArrayList<Candy> combos = new ArrayList<Candy>();
	        int corAnterior = -1;
	        for (int yy = 0; yy < HEIGHT; yy++) {
	            int cor = TABULEIRO[xx][yy];
	            if (cor == corAnterior) {
	                combos.add(new Candy(xx, yy, cor));
	            } else {
	                if (combos.size() >= 3) {
	                    Sound.Combo_2.play();
	                    processCombo(new ArrayList<Candy>(combos));
	                    encontrouCombo = true;
	                }
	                combos.clear();
	                combos.add(new Candy(xx, yy, cor));
	            }
	            corAnterior = cor;
	        }
	        // Verifica combo no final da coluna
	        if (combos.size() >= 3) {
	            Sound.Combo_2.play();
	            processCombo(new ArrayList<Candy>(combos));
	            encontrouCombo = true;
	        }
	    }
	    return encontrouCombo;
	}

	// Método para processar um combo
	private void processCombo(ArrayList<Candy> combos) {
	    int comboSize = combos.size();
	    Sound.Combo_2.play();
	    int pointsEarned = 0;
	    
	    // Calcula pontos baseado no tamanho do combo
	    switch (comboSize) {
	        case 3:
	            pointsEarned = 1;
	            System.out.println("Combo de 3! +1 ponto");
	            break;
	        case 4:
	            pointsEarned = 3;
	            System.out.println("Combo de 4! +3 pontos");
	            break;
	        case 5:
	            pointsEarned = 5;
	            System.out.println("Combo de 5! +5 pontos");
	            break;
	        default:
	            pointsEarned = comboSize; // Para combos maiores que 5
	            System.out.println("Combo de " + comboSize + "! +" + pointsEarned + " pontos");
	            break;
	    }
	    
	    // Remove as peças do combo
	    for (int i = 0; i < combos.size(); i++) {
	        int xtemp = combos.get(i).x;
	        int ytemp = combos.get(i).y;
	        TABULEIRO[xtemp][ytemp] = new Random().nextInt(12); // Gera novas peças
	    }
	    
	    combos.clear();
	    Game.points += pointsEarned;
	    Game.frame.setTitle("Candy_Crush_Couti - Pontos: " + Game.points);

	    // Reseta o cronômetro ao pontuar
	    startTime = System.currentTimeMillis();
	}

	// Método para Game Over
	private void gameOver() {
	    isGameOver = true;
	    // Limpa o estado de seleção do mouse quando der game over
	    Game.selected = false;
	    Game.previousx = 0;
	    Game.previousy = 0;
	    Game.nextx = -1;
	    Game.nexty = -1;
	    System.out.println("Game Over! Você não pontuou em 15 segundos.");
	}
	// Método para reiniciar o jogo
	public static void restartGame() {
	    isGameOver = false;
	    Game.points = 0;
	    startTime = System.currentTimeMillis();
	    Game.frame.setTitle("Candy_Crush_Couti - Pontos: 0");
	    boolean temCombo = false;
	    do {
	        for (int xx = 0; xx < WIDTH; xx++) {
	            for (int yy = 0; yy < HEIGHT; yy++) {
	                TABULEIRO[xx][yy] = new Random().nextInt(12);
	            }
	        }
	        Tabuleiro t = new Tabuleiro();
	        temCombo = t.findHintCombo() != null;
	    } while (!temCombo);
	    System.out.println("Jogo reiniciado!");
	}

	
	public void render(Graphics g, int blinkX, int blinkY, boolean blinkState) {
		for(int x = 0; x < WIDTH; x++) {
			for(int y = 0; y < HEIGHT; y++) {
				boolean isBlink = (x == blinkX && y == blinkY && blinkState);
				if(isBlink) {
					// Pisca: desenha um retângulo branco mais transparente sobre a peça
					g.setColor(new Color(255, 255, 255, 64)); // Branco com alpha 64
					g.fillRect(x*GRID_SIZE + 24, y*GRID_SIZE + 24, GRID_SIZE, GRID_SIZE);
				} else {
					g.setColor(Color.black);
					g.draw3DRect(x*GRID_SIZE + 24, y*GRID_SIZE + 24, GRID_SIZE, GRID_SIZE, false);
					int doce = TABULEIRO[x][y];
					if(doce == DOCE_0) g.drawImage(DOCE_0_SPRITE, x*GRID_SIZE + 10 + 22, y*GRID_SIZE + 10 + 22, 25, 25,null);
					if(doce == DOCE_1) g.drawImage(DOCE_1_SPRITE, x*GRID_SIZE + 10 + 22, y*GRID_SIZE + 10 + 22, 25, 25,null);
					if(doce == DOCE_2) g.drawImage(DOCE_2_SPRITE, x*GRID_SIZE + 10 + 22, y*GRID_SIZE + 10 + 22, 25, 25,null);
					if(doce == DOCE_3) g.drawImage(DOCE_3_SPRITE, x*GRID_SIZE + 10 + 22, y*GRID_SIZE + 10 + 22, 25, 25,null);
					if(doce == DOCE_4) g.drawImage(DOCE_4_SPRITE, x*GRID_SIZE + 10 + 22, y*GRID_SIZE + 10 + 22, 25, 25,null);
					if(doce == DOCE_5) g.drawImage(DOCE_5_SPRITE, x*GRID_SIZE + 10 + 22, y*GRID_SIZE + 10 + 22, 25, 25,null);
					if(doce == DOCE_6) g.drawImage(DOCE_6_SPRITE, x*GRID_SIZE + 10 + 22, y*GRID_SIZE + 10 + 22, 25, 25,null);
					if(doce == DOCE_7) g.drawImage(DOCE_7_SPRITE, x*GRID_SIZE + 10 + 22, y*GRID_SIZE + 10 + 22, 25, 25,null);
					if(doce == DOCE_8) g.drawImage(DOCE_8_SPRITE, x*GRID_SIZE + 10 + 22, y*GRID_SIZE + 10 + 22, 25, 25,null);
					if(doce == DOCE_9) g.drawImage(DOCE_9_SPRITE, x*GRID_SIZE + 10 + 22, y*GRID_SIZE + 10 + 22, 25, 25,null);
					if(doce == DOCE_10) g.drawImage(DOCE_10_SPRITE, x*GRID_SIZE + 10 + 22, y*GRID_SIZE + 10 + 22, 25, 25,null);
					if(doce == DOCE_11) g.drawImage(DOCE_11_SPRITE, x*GRID_SIZE + 10 + 22, y*GRID_SIZE + 10 + 22, 25, 25,null);
				}
				
				if(Game.selected) {
					int posx = Game.previousx/GRID_SIZE;
					int posy = Game.previousy/GRID_SIZE;
					g.setColor(Color.RED);					
					g.draw3DRect(posx*GRID_SIZE + 24,posy*GRID_SIZE+ 24,GRID_SIZE,GRID_SIZE, false);
				}
				 if (isGameOver) {//AQUI PARA INICIAR NO GAME OVER
				        g.setColor(Color.BLACK);
				        g.setFont(gameOverFont);
				        g.drawString("GAME OVER", 90, 120);

				        g.setColor(Color.black);
				        g.setFont(restartFont);
				        g.drawString("Pressione 'R' para reiniciar", 125, 170);
				    }
				}
			}
		}
	
	// Método para verificar se um movimento resultará em um combo válido
	public static boolean isValidMove(int fromX, int fromY, int toX, int toY) {
	    // Verifica se as posições são válidas
	    if (fromX < 0 || fromX >= WIDTH || fromY < 0 || fromY >= HEIGHT ||
	        toX < 0 || toX >= WIDTH || toY < 0 || toY >= HEIGHT) {
	        return false;
	    }
	    
	    // Verifica se as posições são adjacentes (não diagonais)
	    int deltaX = Math.abs(toX - fromX);
	    int deltaY = Math.abs(toY - fromY);
	    if ((deltaX == 1 && deltaY == 0) || (deltaX == 0 && deltaY == 1)) {
	        // Faz a troca temporária
	        int temp = TABULEIRO[fromX][fromY];
	        TABULEIRO[fromX][fromY] = TABULEIRO[toX][toY];
	        TABULEIRO[toX][toY] = temp;
	        
	        // Verifica se há combos após a troca
	        boolean hasCombo = checkForAnyCombo();
	        
	        // Desfaz a troca
	        TABULEIRO[toX][toY] = TABULEIRO[fromX][fromY];
	        TABULEIRO[fromX][fromY] = temp;
	        
	        return hasCombo;
	    }
	    
	    return false;
	}
	
	// Método auxiliar para verificar se há algum combo no tabuleiro
	private static boolean checkForAnyCombo() {
	    // Verifica combos horizontais
	    for (int yy = 0; yy < HEIGHT; yy++) {
	        int count = 1;
	        int currentType = TABULEIRO[0][yy];
	        
	        for (int xx = 1; xx < WIDTH; xx++) {
	            if (TABULEIRO[xx][yy] == currentType) {
	                count++;
	            } else {
	                if (count >= 3) {
	                    return true;
	                }
	                count = 1;
	                currentType = TABULEIRO[xx][yy];
	            }
	        }
	        if (count >= 3) {
	            return true;
	        }
	    }
	    
	    // Verifica combos verticais
	    for (int xx = 0; xx < WIDTH; xx++) {
	        int count = 1;
	        int currentType = TABULEIRO[xx][0];
	        
	        for (int yy = 1; yy < HEIGHT; yy++) {
	            if (TABULEIRO[xx][yy] == currentType) {
	                count++;
	            } else {
	                if (count >= 3) {
	                    return true;
	                }
	                count = 1;
	                currentType = TABULEIRO[xx][yy];
	            }
	        }
	        if (count >= 3) {
	            return true;
	        }
	    }
	    
	    return false;
	}
	
	// Retorna a posição de uma peça que pode completar um combo (dica)
	public int[] findHintCombo() {
	    // Procura horizontalmente
	    for (int y = 0; y < HEIGHT; y++) {
	        for (int x = 0; x < WIDTH; x++) {
	            if (canFormCombo(x, y)) {
	                return new int[]{x, y};
	            }
	        }
	    }
	    // Procura verticalmente
	    for (int x = 0; x < WIDTH; x++) {
	        for (int y = 0; y < HEIGHT; y++) {
	            if (canFormCombo(x, y)) {
	                return new int[]{x, y};
	            }
	        }
	    }
	    return null;
	}

	// Verifica se a peça em (x, y) pode formar um combo ao ser trocada
	private boolean canFormCombo(int x, int y) {
	    int cor = TABULEIRO[x][y];
	    // Testa trocas com vizinhos
	    int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
	    for (int[] d : dirs) {
	        int nx = x + d[0];
	        int ny = y + d[1];
	        if (nx >= 0 && nx < WIDTH && ny >= 0 && ny < HEIGHT) {
	            // Troca temporariamente
	            int temp = TABULEIRO[nx][ny];
	            TABULEIRO[nx][ny] = cor;
	            TABULEIRO[x][y] = temp;
	            boolean combo = checkForAnyCombo();
	            // Desfaz troca
	            TABULEIRO[x][y] = cor;
	            TABULEIRO[nx][ny] = temp;
	            if (combo) return true;
	        }
	    }
	    return false;
	}
	
}
