/**
 * 1. 상어 위치 저장
 * 2. 물고기 위치 저장
 * 3. 현재 위치에서 물고기를 먹으러 갈 수 있는 공간 확인
 * 4. 먹을 수 있는 물고기 체크
 * 5. 
 * 	    먹을 물고기 없으면	-> 종료
 * 	  1마리다  		  	-> 그 물고기 먹으러 이동
 * 	  2마리 이상이다		-> 최단 거리에 있는 물고기. 여러개면, 가장 왼쪽
 * 6. 먹을 물고기 없을 때까지 3~5 반복
 *  
 * @author charles_window
 */
import java.awt.Point;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

public class Solution_16236 {
	//상어 node
    public static class Shark {
        int x, y;
        int lv;
        int fcnt;

        //instantiate shark node
        public Shark(int x, int y) {
            this.x = x;
            this.y = y;
            this.lv = 2;
            this.fcnt = 0;
        }
    }

    public static int N;							//size of the map N*N
    public static int[][] sea;						//map N*N
    public static int[][] check;					//check the shark has been
    public static int[] dr = { 0, 0, 1, -1 };		//북,남,동,서 순으로.
    public static int[] dc = { 1, -1, 0, 0 };
    public static Shark shark;						//shark node
    public static Queue<Point> q;					//bfs 필수 구조. first in first out

    public static void main(String[] args) throws Exception {
        int sum = 0;
        int cnt = 1;
        INIT();
        while (cnt>0) {
            cnt = bfs();
            if(cnt>0)
                sum += cnt;
        }
        System.out.println(sum);
    }

    /**
     * 시작 하는 것. 사용자가 입력했던 input을 다 저장하는 것.
     * @throws Exception
     */
    public static void INIT() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;
        N = Integer.parseInt(br.readLine());		//맵 사이즈
        sea = new int[N][N]; // 맵 초기화
        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < N; j++) {
                sea[i][j] = Integer.parseInt(st.nextToken());
                if (sea[i][j] == 9) {
                    shark = new Shark(i, j);
                    sea[i][j] = 0;
                }
            }
        } // INIT END
        
        br.close();
    }

    /**
     * r과 c이 맵 안에 있는지 확인
     * @param r
     * @param c
     * @return false 맵 안에 없다면. true 맵 안에 있으면
     */
    public static boolean isIn(int r, int c) {
        return (0 <= r && r < N && 0 <= c && c < N);
    }
    
    /**
     * 상어가 fish 먹기
     * @param fish the fish that will be eaten. The location of the shark is at the location where the fish was
     */
    public static void eatFish(Shark fish) {
        // 작은 크기의 물고기만 먹을 수 있음
        shark.x = fish.x;
        shark.y = fish.y;
        sea[fish.x][fish.y] = 0; // 물고기 죽임

        shark.fcnt++; // 먹은 물고기 수 카운트
        if (shark.lv<7 && shark.fcnt == shark.lv) {
            // 자신의 크기만큼 물고기 먹으면 성장
            shark.fcnt = 0;
            shark.lv++;
        }
    }
    
    /**
     * the main algorithm of this program.
     * @return the number of the fish the baby shark ate 
     */
    public static int bfs() {
        check = new int[N][N]; // check 변수 초기화
        q = new LinkedList<Point>();					//bfs 할때 필요한 그 stack. first in first out
        q.offer(new Point(shark.x, shark.y));			//q.offer == 해당 큐의 맨 뒤에 전달된 요소를 삽입함. list에서 list.add와 같은 말임.
        check[shark.x][shark.y] = 1;

        int FishCheck = 0;								//the number of the fish in the sea map
        Shark fish = new Shark(N, N);					//new shark initiation
        loop: while (!q.isEmpty()) {
            int r = q.peek().x;							//q.peek == 해당 큐의 맨 앞에 있는(제일 먼저 저장된) 요소를 반환함
            int c = q.poll().y;							//q.poll == 해당 큐의 맨 앞에 있는(제일 먼저 저장된) 요소를 반환하고, 해당 요소를 큐에서 제거함. 만약 큐가 비어있으면 null을 반환함.

            for (int d = 0; d < dr.length; d++) {
                int nr = r + dr[d];						//북(0),남(1),동(2),서(3) 순으로. nr == new row
                int nc = c + dc[d];						//북(0),남(1),동(2),서(3) 순으로. nc == new column

                // 지나갈 수 있는 곳: 자신보다 큰 물고기가 없는 곳
                if (isIn(nr, nc) && check[nr][nc] == 0 && sea[nr][nc] <= shark.lv) {
                    check[nr][nc] = check[r][c] + 1;
                    q.offer(new Point(nr, nc));

                    // 위치가 더 커질 경우, 더이상 확인할 필요 X
                    if (FishCheck != 0 && FishCheck < check[nr][nc]) {
                        break loop;
                    }
                    
                    // 처음 먹을 수 있는 자기보다 물고기가 발견 되었을 때
                    if (0 < sea[nr][nc] && sea[nr][nc] < shark.lv && FishCheck == 0) {
                        FishCheck = check[nr][nc];
                        fish.x = nr;
                        fish.y = nc;
                        fish.lv = sea[nr][nc];
                    }
                    // 같은 위치에 여러 마리 있을 경우, 가장 위의 가장 왼쪽 물고기부터 먹음
                    else if (FishCheck == check[nr][nc] && 0 < sea[nr][nc] && sea[nr][nc] < shark.lv) {
                        if (nr < fish.x) { // 가장 위에 있는 거 우선권
                            fish.x = nr;
                            fish.y = nc;
                            fish.lv = sea[nr][nc];
                        } else if (nr == fish.x && nc < fish.y) { // 다 가장 위일 경우, 가장 왼쪽 우선권
                            fish.x = nr;
                            fish.y = nc;
                            fish.lv = sea[nr][nc];
                        }

                    }

                }else if(isIn(nr, nc) && check[nr][nc] == 0) {
                    check[nr][nc] = -1;
                }
            }
        }
        // idx 초과 안날 경우
        if (fish.x != N && fish.y != N) {
            eatFish(fish);
        }
        
        return (FishCheck - 1);
    }

}