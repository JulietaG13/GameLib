package model;

import adapters.GsonAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import entities.responses.ErrorResponse;
import entities.responses.StatusResponse;
import interfaces.Responses;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne
    private User owner;

    @Column(nullable = false)
    private String description;
    
    @Column(nullable = false)
    private LocalDateTime releaseDate;
    
    @Column(nullable = false)
    private LocalDateTime lastUpdate;

    //TODO(gameLogo, gameBanner)

    @ManyToMany(mappedBy = "games")
    private final Set<Shelf> inShelves = new HashSet<>();

    @Lob
    @Column(nullable = false, columnDefinition = "CLOB")
    private String cover = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAkACQAAD/2wBDAAIBAQIBAQICAgICAgICAwUDAwMDAwYEBAMFBwYHBwcGBwcICQsJCAgKCAcHCg0KCgsMDAwMBwkODw0MDgsMDAz/2wBDAQICAgMDAwYDAwYMCAcIDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAz/wAARCAAKAAoDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD9BbX/AILufBvVz8d9WtPjx+zzH4Z8D2cUHhIXep39vqc+orBMLkXlu8Qe5t/tAgET6cs5dDJ/FtByvhb/AMHN/wCx/dfDLw5J4w+OnhWHxdJpds2tpo/hzX205L4xKbgWxmsllMIl37DIqvt27gDkV/OF/wAF2PCel+B/+CvXx90vRdM0/R9MtvFMphtLG3S3gi3Rxu21EAUZZmY4HJYnqa/qH/ZI/wCCXH7MviT9lL4Y6jqP7OnwJ1DUNQ8J6Vc3V1c+AdKlmuZXs4meR3aAlmZiSWJJJJJoA//Z";
    
    @Lob
    @Column(name = "background_image", nullable = false, columnDefinition = "CLOB")
    private String backgroundImage = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD/4QKgRXhpZgAATU0AKgAAAAgAA4KYAAIAAAA5AAABPodpAAQAAAABAAABeOocAAcAAAEMAAAAMgAAAAAc6gAAAAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAwqkgdmFsd2VydHkgLSBodHRwOi8vd3d3LnJlZGJ1YmJsZS5jb20vZXMvcGVvcGxlL3ZhbHdlcnQAAAAB6hwABwAAAQwAAAGKAAAAABzqAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAP/hA6RodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvADw/eHBhY2tldCBiZWdpbj0n77u/JyBpZD0nVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkJz8+DQo8eDp4bXBtZXRhIHhtbG5zOng9ImFkb2JlOm5zOm1ldGEvIj48cmRmOlJERiB4bWxuczpyZGY9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkvMDIvMjItcmRmLXN5bnRheC1ucyMiPjxyZGY6RGVzY3JpcHRpb24gcmRmOmFib3V0PSJ1dWlkOmZhZjViZGQ1LWJhM2QtMTFkYS1hZDMxLWQzM2Q3NTE4MmYxYiIgeG1sbnM6ZGM9Imh0dHA6Ly9wdXJsLm9yZy9kYy9lbGVtZW50cy8xLjEvIi8+PHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9InV1aWQ6ZmFmNWJkZDUtYmEzZC0xMWRhLWFkMzEtZDMzZDc1MTgyZjFiIiB4bWxuczpkYz0iaHR0cDovL3B1cmwub3JnL2RjL2VsZW1lbnRzLzEuMS8iPjxkYzpyaWdodHM+PHJkZjpBbHQgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj48cmRmOmxpIHhtbDpsYW5nPSJ4LWRlZmF1bHQiPsKpIHZhbHdlcnR5IC0gaHR0cDovL3d3dy5yZWRidWJibGUuY29tL2VzL3Blb3BsZS92YWx3ZXJ0PC9yZGY6bGk+PC9yZGY6QWx0Pg0KCQkJPC9kYzpyaWdodHM+PC9yZGY6RGVzY3JpcHRpb24+PC9yZGY6UkRGPjwveDp4bXBtZXRhPg0KICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIDw/eHBhY2tldCBlbmQ9J3cnPz7/2wBDAAMCAgMCAgMDAwMEAwMEBQgFBQQEBQoHBwYIDAoMDAsKCwsNDhIQDQ4RDgsLEBYQERMUFRUVDA8XGBYUGBIUFRT/2wBDAQMEBAUEBQkFBQkUDQsNFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBT/wAARCAFIAUgDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwDmoZrvzy/2iTk4xmr9vPdNdIPPbHekht2a5nBH3Rx9as28Z85SF74r+fL+/c/dPYRtc7Hw7GZN/mfMOoq89qMsQDVjw7Cv2dht/hq20K/NXoxs1c8WpFJ2Rjx22Oc81NHCvpir624PGKTy1XqK1gorc5uYp+SBwKDCB6itHyV25xzSrCrdRV3ihNsoLb7vWk+xn61p+Sq9BSeX160e0MdTOWxPrUcdiS3Nasals4HSlt4WZsEc07qW4rMpfYY9vWkWxI+6citWK3GeRUv2cfwjFRKS6FWZjC1fPXimmIt/FWz5ApFtY933ayUrC94y47Vi3IpXt1WthLdV6ikazR+tdKaauTaRmfY4mXA+UUkdiqjrxWn5CNwozUot0UdKq6FqZDWq5O1OKPshj3fuwa2VhGwjHFJ9nHei8S7MyfsKt1GKY1lEp4BIrb+zhuSOKiaFBwOan3RWkZa2MO37pqWPTYj2rUFuFXgVNHb+1K6HZmQunx9McUv9mxryBWu0O3+Co2hLr0xS54j5WZDaau/OzP4U5dORusQFbiwqq89aUwxheKmVpC94wv7NTbyopV0uPGfL/StkINuAKkWH5TTXKg1ML+y4/wDnjT10uLbjya2VjDNgdfrR5Pl9BSlYNTDGkoG/1X6VZ/sWJhnyhWvFbbj04p7JtbGKzFqYi6XCox5QP4Uf2RE3/LEY+lbZtA/O7bSrart6mtFyoNTG/sW32/6tc/Smf2LB/wA8l/KttbVV5HWpPs4qXZj1Mj+x7ZY8eUKK2lt1K80UaBqeB2aiW6kJ6NUtptaRj2DcGqFnKRCXB5q7pjBrEnvuryG9bH1/2T0Pw+v+js2ONtXAuc+9VPDMn+gsT0K1edS1d0HpY8Kp8RBuO7IFO8r7RHk8VMijuBSqO9aE2RGseBSxr+FTY70wqG9qrUycb7D0jGOaRhtDcU9fumk2llIxmriuYy5Rtuud2BViOPafeoLceXuyasqwPNS9NhAw28gcUcNyaWT7vPFOhG5ajR7GgnlrR5YU5IqwqgZpkjButBFw8uk8v2qZcUFsVpzW0EnciitgW44qVoFpVOOe1DN70rlciGrH5a470GEN0pWJxmlDr607kvQb9nO3rUbWvzZ/rVlcN/FQ0fakFxnl5XFOWMxjg09EzStwtSK4SMNnTmoW+bgHmpG+7TAu00Go/biPkc0scOVwakjXzVoY+WaFoSxqxgVPGi7TUS/N0qdf3YLd6DMg+z4bOOKdGu/tUsbFlbmnqoFAEayLGcUoi8xsmkWAltx6VKflouBE0ShvapFiCj1pG+9U6AbeaQEPl98U7Z8tS4+Wm/w0AIqjb0oqxGoZaKAPmCOMxwgLzV7T4zHbhT3OaZpkYki2tyw5q/s2suBhelebK3MfV7KzPQPD4U6b07Yq8WCx571neG8rZ4PIq7ID+FdtPa55dRK9w3elPU1FD/q8nmhWzWpxlzaNuM02RR2qLO3nNBjLnA61qClY1/Duiy6+xjiUlvUV1lx8O7qytwWiZge9U/hbqCafrkcZGATjmvoeOKO4Xa4DRkZFe/hMGpq54mKxfI9D5Yv9DktJmDq30rP8to/XFfTHiDwDZ6luZE2ynvivN9c+HMlqrnGB24oxGXPoZUcWqqPNFYMvz1NHjbxU2q6U9nlSuDVaFDGuD1r5rllTdme16EvO01Cq5PNS+vHShQOtXYyFXPrQxNLJ6imj7uTRYexIv3aFUEc1Hu+UkdqdGrMuBRY0VQmVcr0pPL9qRZDGMU77RmlcYi7V7UpkJaoVbdJ7VMy5bIppmbVhRKe1AJaoVz5mO1SrwtFiSZF+XmoWqZfuYqGT5WOaLGjZYtflXiorgndUluw202TCtzRYi9yS1BKc9albofSkhxt4p0inYSO1SIFwFbFPWoFzUgyKNRb7Fhm+XioHVm6UqybjipFG6gqxCytuzUy520oXnBpFPagLC87cU3B208cnFP2/LQIWI7VopD93AooA+atPu1hYHPJGDWozNNGkgHU5rmpoWhUAjBAzXQWMpezj5xXk/bsz6qptdHoPhWTfYHPXrV6Zg2UWsfwyfLjYZ+UjFagyzMRXpU17p49V62I/M8k7Qam47UyYL1xzSQLurQ5rj2b5anhYrLkdajMOHz2qVcLzVWFY6Dw/Mi3UUv3WDV9DeH79LyzhAkBO3nmvmfT7gRqCOcHNeoeA/EeyQRu2OMDNfT4CryqzZ4uOoc0Lo9rt8su0tk4zWT4i0tp7QsmTV/S7yOSMSZzkYrTk2SR7cjFfTxp+2jdHzlNulI+evFGju24yJsNcBeRNFNjOK+m/FHhFNShkcDB9K8U8R+C7iEPI0bAL0r5fG4SzufT4fEKpocUsnyn5utEh2rwea29N8LyX93+8BjQfdJqLxFYWWlt5Rl3yeor5+cFE9RU3LYy1bcuKXcOlReYu3IPFG4ZzXPzJ7FSotFiNd0bYpyb1/hqBpH2naMCljMsnXIH1pp3J5LEm1mGW4pyx1GzMwxmmq0sbY60g1JtvzcVIMGo43G7aalOM4BphyibcDPej+Gnso20pjVl4NPUXIxiv8oFPIDKTTdvSngbR7UzO4kfCnFKq7uT0qSNhtIAHNM+6uDRdCJPMKr8vFNZpZMjPWmqwbpTSx7E1O+xMpWHZK9WqaNt2faqp+b71WbXeyEcYq1F2uKm+5IB83FLlu1MXOcVKGBqGdaWlyPzX34xU4U7s1HuHXHNS9RmkZtiMSFz3pyzbV5oGM89KSYDbxQZig570VErYXFFAHzPLumlbPzADFalkC0KDpiqNn96QnGK0rFt0eCK8mXx3PqZpxidv4eOLbNbCgruU9KyfDePsmTWvMfmGK9On8J41V63GSD5fWiNSvIqQYVcdaarHbVnOHmHPNOxxjNR7TI2cU8oFqyXcsW7COPbnitvSNSFvMkpfaFrAaQLDnHNR2c27ls7fSu+hUsc1T3lY+iPA3iiLULfDPgjrzXfWN4khU7sivlTw/wCI7nT7k4O2MNk/SvZfD/jdNQgRoiMDrX1uGxFo7ni4jDXd0j1A3yMxJb5e9edePvG+k6TZzo7KxAzWd4r8epp9jMYnGFXr718jfEz4lS6h54MxGWwOa58biFyWO7BYKTdz0TUvi3E0sixP5ag4GDXOSeKpNSvA5fKn15rws6rLcTqiSbgDnNdxouorDaDzCAR718PWm5Ox9rSwns9z0m71yOFFBOKt6frENyvJxXlGp6s9ztCN8v1re0rVFtdPBcZf1rlpqVrmk8OnsdrqGti15XkfWsz/AITYNkKelcrJrwn3l+naoorq28wBE5xkjvWt2jn+rnYx+IribLZwBR/wkF2zY3cfWsq3uEe3KthDVWS8jibZWnMX9WVr2Ot0/WpZOW61pwasofDnJrmLK4Vk4xn2p0kwZg4zk07nLKjbY7eK4WQgButToTHiuUsZpnZMHrXRQiWRkXk5710xXNuYezZfzxkgCoZJNynac4rRt9DlkwWOQfercmhxxqMMq+vNTJdjk9izGtdvc0sje1TXFh5GSGAH1qIKVzmReOa57SexMqfKQxsQvINSRtuUnrt61Xmvbe1j3vOpb+6Kdpd0NUcJFEVDd+9dlGi27GFRGppOnyX04CoZA3Wu2uPAL2ul+eOuM9K1/AmgrYxtJImccjNdPq10sulTBewwK936qlTv1PM9vaVjwub93IyEY5xUK/nVvWI/9IbPHzdqq42LmvnKkeWVj2acrwuOHK57VOnK4qBW+THeneZtrMz1Jd1Lt+XJqAHNT7v3eKCSGikWTHFFAHzjpe2WFye9Xo/3agj1xVTS9qwsMc1Pavu3A9A3FeTL4j6qpLmid34bybPBrZ2g7c1jeGWC2vNbMnRSK9On8J4lXew6P7/PSmyfex2qUYLc018M2BVmA3JHRqBjbyaj2/ep0eO+ady9GP6rmmLjdjoKkcHaMd6YsY2+9bU1rYfL2J4ZOx4B61saDrEmltw+Y/SuVu9Q+zCQsnSq9h4ot5H2t64r3qTtHU5JK52HjK88zTpLhG3Qlfu18l+N5ZYdWkD/ADxM+QPSvqCG+tb5fKZh5JGNpNfOvxosRoevRNFIJY5JMkDsKjEyUo6npYOVjH0S1kWze6K/x8LjtU7zXM0DONwHtWx4VvYb6FlABO77uK0NfsU02N/lHlsM4FfNSjrdH0EW+pykerSRxwFm5Y4rUOvzSEKjYUHFcq2+S4STpFGcmpo71ZrpViBJY8Yq4bWRodnJcOtuGB+Y9atWN5DDHJK4ZZAMCpNL0OSeBZZgVXGeazdeWK3uliXJLnAANJ030MFJXszS0vVJbrewY47Zo1LVjbyIpOSxwKxrzV4tFjSELh2rTXTRqNrHcOccZGaz5WnZnTzRtZHX6TdBIEkY896sW+pRzXDAttArlLe4ZYxGWyRUbaktnIVPOTgGmpa2OWUVLY9Ah1yG1bcr8Cqt58THsJAsfPpXN2UieWWlGR1rD1S4gh3yHJ9K64uxg4HcTfFi/ZTmRkGMjBqbT/H2o6ldKiTsc/e5rxCTXpZrpwz7Vzx9K6/wBqyyXcpJxt7npRSTqOxxcrW52nifx1e6bGYzOxY8dapaX4t1jVMqpcgr1pNP0FfFGryiRhJGrda9U0TwvaWEKRRQAsRjIFetSw+lzCpaCucroej6jeSxy3Tk56qa9x8A+D2ZoZMYUdeKk8J+C45PLeZAV+legxXEGjwNHCoBHTFevh6FtT5qpXu7ItKv2CBl3DpiqE0wFnIr8bhkVWv9U81VwetQatdLDpo7tjFdFWPunNdc1zgNejRZJDnvxWMjM0ZPYVp6tJ5zsDWcq4Vk7V8dil77PZo/ANUmnryOaGAXmhfu1yrVXOiOoqsAad5ny1BuqT+HNIyasO2/LnHNFQrKdhFFA+U+dbBsXBP8Bq5uCuqqOSc1Rsf4fpmruP8ASkFeY4+8fRR10Z33hyPFll+K1iwUrg8Vl6CdtrzyMZ5q/JGSq47V2027WPKnZyZMzbmyKXeN+ecVDHletO8w1sc4snJJFCsVpNwNIOaCrEzSKAParVrGJBgCqAj8zg1o6dIiqQWwRXfRim7mak0UtV0Xzrd9x5rzjVNLms7giJu+a9hjuIpslzn61zuvabBNllwCa7nLliKPvOzPM7jWLrS83GWOeq9q828YXcutzO0jFsHINer+I7RJLA4HTrXk+qMnmyR5CmvFxFdtWR7uFopbDfCt9/Z90EA7cn3rqdW1T7Vp8hdgzAY5rhYM2KmbduArP1DxM9xcpDE2AxwRXPCLkrnoXktzV02xutakFta/Puf5u3FegaX4ZstEljFwqiRU5J9ag8OabDpGi/bo+JmGTXH+JPGFzJOUZtjE9T6VtFcg1NdT0231xb6GeOIjyl+UEetZ9j4an1LUmmmVtkYyprmfh7eeZaylv3pL5K19QeF/DVvceDWnaPEzLwcda9ehRdRXPHxNX3tGfMuoaHLq2tKSv+rf9BXoVnpfnWaRlMIowSKTVtGTS4rmUf6zLEVB4N8SDUIZoTyQcCuPEUnDUqjUU476lXXLGPR43cHJAB/OubsJkvroK3zgNzXU+LGN1HdxbfuoDXn3hO4b+1Hib5SG5Y146bUtT0qceU6XXNa+xx+WoxxivP7zxBcTl0kGFNdLrEhuL+WPG/b/ABVjSaPE0bO3DV1wkupcvI5efUVmkcnj6UzTfGM2meZDGcGXjPetW60WDT7WSeQce9c/oOkv4m16KOzhMhLdFFenRjrc4665VdH0H8C5rq+hLMzEsc5r6q8GeGRNDvlGcdK80+Cvw3GjaTF5sRSbuDXvmlJHYxbV4r36NNuJ8xXqNuzLdraLaxlBwO2KqXPU5X/gVaMfz1FNMkbbZFytd8Pd2PF0i7oyFtxcSIvp1qr4gZMhA4Cj1Nb2nx2jTO27g9ea5/XF07zG3S5/GprS93UIxTdzidSKLIwU5qircsexq3qk9r5ji3PHqaorKNtfG4iznI9mh/DJJDwBQv3aRVMhzS4K1x36HStFcZsNSFTtxR2zRn3oI3ItrKpG0UVIzHGaKDQ+crLAZD2xirka5vG9qz7DP7vNaMfF0x7ZxXG1aR70NNWd5po/0dCOm3mtKGXdkDtWbpq4gX5uNtW0yrkV0x0PCk2pskbJ6U4gydttN3n0pjuyJnNaOwhIVY9SamVgKijLNyTxT80b7FXJoT81SMwXdjiooeashBzla9XDU3I5pSS2K0dwckdFqrqUmMsCSKtzIFjbHWsy4JaFs1vWhpZGdKV3c5rWH3Wso2ivIdZsjNdSkDGP4q9T1+ZnlKrxnrXDaxZzRzZKYDdq8iVNSPpKNZRPMddvntbcwxHcehrmdLaWTXId4wN1d5qOgvE00z8/NwMVR0DwveXXiCCVYS6FuBiu6jTSVrBVxC3PRtFW5vbJoFDGIrxXCeMNHu7rVobO1XdMTj3r6f8AC/gWWx0NbmOHfI4xtxW18MvgOLrxA2parb5fOU3DpXXDBtnjVMb5nl/w7+Fs/hvT4HvwfPnAOxvevqXwvon2Xwf9nkX58YHtUmqeDYpdUTMY2QgBOOldHaW6w2Aj5POK9inQ5Ynj1sWubU+b/izpKaLGCo/1hIP41x+g+GY9GjW4z/rBkV718YPCMWraakhHzRnIryXUo3t9PMSru8peTXlYqPMj2Mvm7+8ee+IvEkdheXKswbdxXOaJpr3czXEX3nOSo7VzPia4mvtbeJck7q9W8C6StjYh5h8zp1PrXy1Snyy1PrOaNrowtY0tdNgL7sO33q5ve0m+aP7o/gau/wDEEiSsVwCoOORXP3nhie8XzX/dDtt4zXO52NI8rONuhL4quI9LhXywete8fAn4a6foaxSPEpud+C5Fec+H/Cz2OsQzqNxHVhX0T4WhWGzjKDD5zmvQo19bHFioNRPXtFtVt7c8cmrbXPlrwc/WuIj1qSNNrlvzq9Y6wbyEs2QF6819bRre6fHVoOUjp7TUrnzyD0xnPauitVF9Edy7gK88/wCEqtISYo/mOMZzXS+E766uYmYKwVunFd8JXOaVNRV2dIum2pidYxsIrkvEnhu32tIGrvYdMb7C0mDuPSuJ18TRuyEcVniU+S5xxkr2R5veWSwvIFOB61WjjVV4O6r+qSFbh1PSs9Y1CfKcV8TWk3OR7GH+GxKsgVvapGk+X3qqpLU9ST1qDrW1mTRqNpJPSiQADIFNQkRml/h5oKSuRbvkzRUoQGPpRQSfOVu3CdsdauwndOwP94frWHDNL9sCkYWtqE7bhc92FcM37x7kpaWPQtPB2L6bankk2896ZagCFD/s0+Rd2cDNdEZM8ipvdjll3Co2YtHimcr7Uvm7eBWnK2ZXRNF93pTvvVX+782T0ztommFrhpGVSe2a6qMZPoYynbZFuFtrZJ5rRt2FwMVgrexyrwcmtXTWPlZr3aacd0cMpczJZ4QGYe2ayrhkW3bIxW7GUYNuHOMVzGvX6wKyqtW43LomJcWhkuPmANUrrR5LyaNWiBX1xW/odj/aTZ35rrLfRQtqYSMP2JqYUYs6nVcTxm/8K/apJlMJCxnPSu6+GvguCa6hPljAbqRXoNv4JS7tgoCmRvvV23grwjBo8Owplgc5rrp0oxOSpim4WOr0TwjaRWq7o+AMha6q2tIYUcIgT0pLMjAOOMAVLcOBkZwTXoxikrnz8qsmZ9zboyuSBuNYl1D9lTJPfNdM8QkXmsDxIqwxyNj7qce5qp6RM3LmmkzzX4geKAtubZQC7HArxvxhLLpunzJEN8ki9ua6/wAbT7pDOTtfdwKozaXHqFpCWXdIVya8LEb3PsMO7RPEvBPgWXW9eea4TaAcnIr0jUtJSxm8lP8AVgYrc0PS47GaZANsknpWleaKkNqzS/MfWvAxULu6Pdp1Lqx5jqnhppJ1dBuUnO0Vs/2G2oW8MRj2hetdLHZx3GzYvStzT9KC7civHlCx0xrcpz2j+EYYIThCX7V1ui6a6gKF2+lbFhYQKMbTW1a6WGXctdVOFpHNiMS5RMiaNI+GGTUN1I40+SK3Xl/7vauoh8P/AGpvmNbmm+D4EjMmMnuK+uw8LxPmqmJhDVnFeCfh/cuzT3TFlJyF617f4Y0RbeySMgDPtzUGjafDbxqsYx9a6vT4kWPdj5vSvfp0zwq2LVRWQrW6x25XtXlvxAZbZWZeDXq0zg7hXjXxOutszpnFc2Ye7T0McPLmkeY3kxmmZmqNZk2Y71FGSzNml+zhvmPWvgHdzbPpqOisTRYOalIG3g1WQ/vmx0p27DYzxV2OmUidf0qTb8uKrq2af5jVI4yYedtYg9KKayrtY0UBc+af+WwOcADNa1m3nTRf71ZPl5CN7YNaemyCOaH/AHq43HmkezNWPSVLLBH/ALtTRsVBz0NMjKyQx4/u0rNuXArpjA8uvvYjkkHLyEIg71xvij4oaB4bSQ3F6hcdBmovi1eXWn+D7oQSGNgMhx1r4q8Za0NWtyjyO02cFia+gwGH9q9tDwa9f2J7F42/a8EMklvpMO7AwJM1xPw9+Pmv+LvFHlXt0xi3cLntXh8mjpNGT553g4wK6j4XQ2fh7WhdXU4UZ6tX2NPB0qcdtTzZYmc1dM++vCt201rFIz7gwz1ruNNnCqADXg/gb4n6VcwxRLcRhcYHNeqaLrME21lkVl9mrjqUdbJHZGquQ7qR1WNiBzjNclqtv9qdmxmtmO+jdeGGGXjJqvaxi6m2NgE1xexl1N4Vkti74Rt0hkUAAfhXZTWoWbzB64rG0Gx8udiCuBWvPIzSbe+c0Rp8pdSpzHT6PHnGAPrXSadlbgZ5BrD0GLZHu7VtxuAwK9RWx5srvQ6qCURx4B4qE3Jmbk1ntcsttweabaTbk3V0cytY45QdzobciQYHNU9c0k3MODkqFqWzlEcec1fluC0ZAw2RirfvRMYxfOmz5q8eeHJNT1qKBAURTn2rQbQWsbWIBc7Y/vV6FrukJJePJtwzVXt7FZLV45Rk4wM15k6C6s+ioVLKzZ5FpemSSaxIz8qK6O6sxeWEyEcjpWnqWlrp8hKcDvWZE7efgNkd68etSj3Pbp1lHYoafoXkWY39TV23tPLyDkelal0VjWNAchulWo7MS4IWvFqU0b+2cuhWgbb8u3ityxm6AcA1mTWLo3pTrUvDMBngVVGjeWrOfEVGo7HaaTGM5J4rpbGPAPHB7Vzuix5Uc12mj24kXB619jhVfRHymJqKTsx1jHhiwX6VsWkp6HikSFbfdxiqFpfBtQMAHQ4r3Kcbbnjyia5by2myu7AyK8B+J1yZtadCCBX0Tc2pZW28MRivFviZ4Nu5Lj7SqknvgVw5jH92dGFajLU8kt1xvB+8KTzmVsYpt1byW1yy7iH7im7nVio+8K/PZc0Z6o+roSTJujEjpSNnNJxUpUNRza2OtwbFj+7UpwOarqdmc9KTzCy8UDWm5LKRs4oqKJs8HrRVWMrnzhyqkHrV/TeZogezVVK/M2an03JvY1/2q5l3PoZ6q6PT7VCFj9NtEZIzmi3BEac/w1IF3KAeDXVE8es7y0PPPj3cNa/D/UJlH3Y6+BLe5uNWmeXICBq/Qn4xaVLqvgHVIcZHl1+f+i2v2eS7tQeFbGfpX2uXcqp3PmcZvYrf2TIHdi3DHPFVp7H+H5sVvyRlZDzxVC6kCttPSvY9o5Ox5Nuhj22oXulnNvO8eDng13vhP44a34bmUyTNPF/dLVwdwqbjxxVEyKJCAOO1dMYc+pXNKJ9ZeEP2prLUVRLweWQMctXsPhn4safq8iNBeRbj2zzX5yNhJNy5Uf7NaOieKr/QbjzbW5lyP4d1OVG+xSrH6v6H4l3YO4cgE/jXR22pLKVYuPm6V+e3w5/aquLNRbX4LkADcxr37wj8fdP1JkxcJg/wlulefUoSjudsa0ZbH2Fo+oKtqFLCteG4C/MMEfWvCtD+KVvcxqY3Rl9jXW2vj63niBDhfxrilFrobJJq9z1W41BZIQqkA1bsV/cjmvGX8dCG4XMw2/Wulj+JFqtquHUH61CT7Eytax6X5oHBlx+NX7OXzIxhq8Mm+LllDN++uolH+01Z+pftSeHNDj2SahCT7OK76MX1OTQ9q1lm+1n51wKqfIvv618q+JP24vDmnyNiRZj6hua851b/AIKJ6dEzrZ2kkhrrnh4yje2piqjUrLY+1fEKRyQs2a88vphazEq+0ZxXyPqX7fGpalau1vabFPrXn1x+2X4gvGZhAMZyK8meX8+yPSo4vl3P0T0eZL7YXcfL7129utssa4Zc1+YGn/tneI7f5UiGa6ax/bi8Q7wrJ0pxyuPVGdbMZrY/Ra8jjkjJUjiqdrp4bDOetfEun/twXscYE8Ck966rSf224ZIR5sCgD3pxyune5h/aFSSs2fbmkxpbqMNnFdppUkMUW8uAeuK+KfD/AO2Rpd3GFAUOSADmvXvBvxW1DxZBut7Zgjr8rgcV6FHCqkYVKvtXdn0JdalHDbyO7LsUZyTXldj8U7NviFHp8e1mZsdas3Gi614gs1jaVodwwea5/wAI/s+3mmeNLbWJrgyFGznOa3cWedPmR9Iwxq2XOeQDj61Vv7KG+h2Oitn1FWo1EceN24oAM/SkMi8jA9quVNVYWOb2ji7njPjv4YojvcQKNx7AV49qWmy2MjCRCpr7CuLP7SrFxuB7V5D8UPDMC7pBGq/QV83jMtio81z3cDi+bQ8OXIbnmrC8rmmXcDRzSYOAOlIi4hzu4r4uaUZ2PraVTmHSDcrZpFjO2m7tykCnqTtpIqTFRcL70UsLDkGiqMz5wVss+an0r5r6MjpuqvGpZnx64q3pC7byMn+9WHKfR/ZPUY5QsKZGPlpygNg54qBjut4+f4aVHIGK6ktLo8Wb9+xX1yz/ALU0m9t2OPMXFfn1480c+DfG97Zy/LG7kqcV+hy/OGXHWvl/9qj4XSzkatZxlmjGWIHNfQ5dK3ut6HkYundXPnoq7Zc8A9KzLznnNLZXDSQsJmZXU4CmorhGZs54r6KPus8NRuUrjOztmsxlDE561oXjfKcGslpDuau+FzOW1xwbcr8fSqturDc2MGnJMQxBpVcqprupo45O2w2RRG24Z3Vcsteu9NkVo7h1+hqg7+YeaikiLbcVfuy3Ofmcdz1Xw/8AHbVdBwi3DsvoTXoum/tWS2sKrJuJr5q+xtNMAOSatDQL5lB2MQBnpWM6MZOxvGtLl0Ppu8/ayDYMec+jGsLV/wBq7WtQjaO1Jj9GFfP7Wk0bfvI9re4qxHEfLLEYqXQjEl1pLc6/Wvit4n1lpGfUnjHbaa5ptbvr3JuL6WRvUtVKZSsBx1qgoZfYVUYoqMnJXL8kjqxd5PN+pqWykRyWbC+mKyJJgp65FKlwMcHFWo2NYnUwzIxJ3ZB/hFTrIkjfJEqiuUt7h/MwrYFaS3TRp97mqNuXm2Okt5Ioj/qxmtS3Nvs3FVz9K42G4djndxUy38qcbsCj3ieRo65obSXneVP1qrMwVgkUp2scHmuabUpfTH41e0e8F1qdnFIOGYA/jXJL92c8abvY+mv2cvgjdeNdchmvpGW0UqRjoa/TTwJ4Zs/Dem29vaRoEjGORXy/+z6sOl+H7Ly48DapJAr6Gs/Ewjgwp/WuKpiTrp02eqWN5s4+Uit60ug0KMD0615XpPiTzjt3cV2ek61GseGx0zSjWUtyZw7nYQTD5huySc1bU/NyK5y11QSXA2gY9q37OXzzg10RlY4Jw7lzd+7rhviNZpPpbyHjFduVA+lYPirTRf6c6mqxNJTomVFuE7o+VtUt9s8gB5rOXKptFdN4q0w2V9KOlc5gBs5yfSvzPF0XCVz73CK8LtiRc57VJJ93ikA3Bm6Glf7lccfM6nd7DID8zc0UW8eGLHpRVCsfN9jc71c/7Va+kr5l1H/vVzOl5VpAD/FXWeH13TIe4aiqfRdT0AwlbdAf7tNXiP3qWRi0KZ7jFN2gLVU5e7qeBPWowhk5zWb4m0eDX7KWznG9WGBVuIk0XDbAp6FeldNGryT3M6keaJ8GfGz4cXXg3WnmhhKwE5HFecx3ZupQpGMDJr9C/H3ge18daTJBPGpkP3XI6V8bfFT4Mal4FvGuIozLE5/hXgCvsMFiY1VZngVafKeaX6/ewvFYtx8hY1sT3joohdcP3BrKv4iSWBGK92D10PPn8JmvIfMyOtP80kdarzBlbHQVEsxJ613RVjjeu5cXG6nwgtIpz8ucUyJTxkYrrfA3g268W6xb2trEx5BbjjmolJU1czlT9o7I7P4VfD3/AISq+jLRMU9cV9GR/A60uNNISEIyr6V6F8HPhPbeH9NhV4gJAuDx3r2y08LwtaldgBxg8V5UsRyO6PUpUbR2PzV+KPw3uvDs0shQ7V9q8ue3KqTuyhONtfpX8XPg7Hrml3SLEu5kznFfn38QPBt34X1SS1eE4DcNXTSre03OSvQOPmmVgVFRGDfF05pgjZNzPw3vV6xUyR8iuuJjFcqsZT6ZJIpKnGKha1eNtuOfpXTCMrGAvHrUbWqtz3rS5ojAtrdg2SKutb/xVf8AsoprW3PXikbrQr2zbW56Utw1SqgX5egps1qWGQc0DuUPMfqz11fw++xy+IrU3b7UVgcmsNdMdoy2Minww/ZZVfDLjvmuWstDGPxH6a/C/U7S10W0e2l3xBACK9MtdeW4fCSDbXx7+zP4wbWNHNiZS0iHgE9q+l/D1nLuPbFeBV0PZpanquh3xOcNx9a6AeIJbrUvIgOFUYavPIr4WcYCnB71paTfMJnuAcFutcsanKXUhfoe3+Hbz5V3t81dtpdyM5zXjnhnWsOpzk16ZoOoJKoJxmvSw9TmPIrQOzjYNGR3qrcRieFkNLZzKVTJ61Lxhq9hax1PInHlPG/iF4HEytcLu6ZNeOXljHZsygksDgmvrbVrJL+0kjIG7GOa+fPGvh/7C0xAA3NXyOOwrep9Hl2JSXK3qcEsgaNuaVjlahWMxs49Kdu+XFfJSjaVj6qG1yxHhl5oqKObauDRUXKPmKxwJH7ZORXVeHmCzKPfNclbriTj1xXT6Dn7QgorXvY9iUmlc9GdgY09qiYnbSR58pSaXqp9q1pxUonivVtjITzSzbZl60zcFpVw3QVnGDbsJPSzLFvCUAyODVPxD4WsfFGnPb3NupLLt3MM8VsW9vvVSTV+K2CqTXv4ROjscFakpHxj8Vv2X5jdT3umK2PRa+fPEHwt1rSWk3xS8dMqcV+ps1oHQh1Dof4cVgap4H0jWo3W7tY1z0O2vpKOJkeVLDx2PyiutDvrdikkEn5VWh0W5Zv9Q/5V+mmofs6aHqBZ44F/KqVv+zFpG7/UR/lXasdbc4PY62R8F+Dvhjq3iy8hgWCTYX5O3tX2l8LfhPbeBLW1Y24a5fGXI5GK9j8KfCPSfCQzbWib8dSO9M1aP7HckyR/IOnFc9bEe0Vjvw+FipHUeHYxsWPAzjOa7m3iXykXGc9a8z8Oa1bQ25LthyefpXbW2vWohQiTOPeuDn0seu8M0tDo7vRYLq3kVhuBXFfIn7QnwXGob5rS3ywOSVGa+tbfWIbm3OyQDNV73R4NUgZGiWRiM9K6sPJxPLrR01Px38YeDbnRb6VZonC9BkVg2W75wBjb1Br9JPjF+zmnia0lmgtlSUHOFXmvkHxh8CdR0K4uRJZsmO4U17dOopbngVbp3R5J97gnHGaaqKxwG/WtbVPDN1prHfEx4xytYDC7hfItjj/dNX7REU5yk7SLjRBepoWMbdq/MKjimnm+Qw8/7tWUs72aQKlrIT/sikqsep0ObvZFaWFY5FV/lJp7QrboC7YzXS6f8NfE2uTRNDp8rg/7Jr2PwH+yJreuOkuoxvEv91hWbxEUbxvI+dFee+jMFnC7OTjKg16j8Of2ddc8YNEZ1lhgk6swr7P8A/svaB4QtRLPbxzzgfxKDzXqmn+HbfT7NIbW1jgC9MCvNqYyL0HCneR4R8M/gjZfDOMOrF7kV7Vps0Xkqyr+8PUVBqFi+/EgyfUVViuBayBsHaOteTOq5Ox7FOFlcu65fCGHK8EU/wAP6yGh8tpAHPYmlazTVYS+3NYUelNDeDAIA7iuSZu9dz03w5eytdBVbOPevT9C1grIqbq8g8NOIX44Ndhp9xM10uHrsws+U4atO6ue0aTfyNKuT8vaukt5CyE1wnh+Vi0e5t1drC37njmvoKVTmjqeBWhrYt3CqJEI79a8r+Jmj7oJXQZGc16z951BIrnPFdiJrKZSmVrHFU+anoY4ebp1Lo+Vr1UW4YL+NVywXgmur8SaLFa3EsiJhvSuSkbc5BXGDg1+dYik41Ln6BRq81IUSrnGKKMpszxRXLodCZ8x27fvD/vV0/hxi10v1xXKW7fvG575rq/C+WulwO+a0nG7uz1pv3DvmUhFweKVWG4ipGwIxUDctxURvFWR5dhrISatQR/vMDmkVQ0fHWrelQ7my3WvQw8E3dmU2asNrtC8cVcWH92cd6mgiBI9KLpSvCcCvXUbHFdlRn2rtxzVSSN5BlsVZZircikk+5uGa2jJx2E4N7FywniZdnVvyq7DbosmSTiufjmeFi64yK1bHVo7glX+X60OVznjRs9TSjUySYPArI17QPtkJX7x9hW1HMkrExEMB6VOULYY8GqiJXpy0PCNYE+i3TQlSAxwW9KbJ4oa3SCOE73bjANeo+NNDs7u3ZjH+8Ycn3rgvCPguD+1GabLbW+UGtLa2PZVeLjY7jwjouqassMrFo425xXquk6aLGNA75YDANUfDsS20MaKdqgYxW4iquctu9K607bHgYiScrCXUIlVo/LBY/xYrktX+H1hqzTC5tklz/eUV2yuVXGcmnwxmTfnrTjUkjz5Uz591r9nHQNW3M1lGD/u1zFx+yjoG0f6KMn/AGa+qorLzFBYYx0xUSwqzEMgOPatHUkTToq92fLVr+yb4bTLtaKG9cCtew/Zz8M6fhltI3I/2a99uIUjLZVaxpkCN8qgCsJ1mbKnHm0ON0rwPpWjqq29lGuP9kV0kMYWLYqKuPQVZ2nOdtO8k9QK45VX0OynBFFo1bqv50pjTa2R9KtiPPakeM4wVxXNGNxKmk7owdQsy4zjNc7cWIZ2ycL6V3U2BHjbn8KxbqxWRmxWiSWp103pYx9NnWHMe7FXGt0ZWdGBrM1S3+y27uTsPuaw9N1uV2ZQ+4VnPyNLHQtqFxC5MbbcVu+FfEh+1DzST9a5RbqIzKGkA3etdNo9nF5mUCsfarhJoVRLkPdPC+qJNsZTgV3thdechCmvIvCUwt9oYV6Lpt0iqfm256V7NGp7p8/Whrc6m2m3E889qmurUXFuyt3qrazJu4rTmG6Pjn6V7dOPtIWZ4cvdldHivjvw55EkrqvGM14vfW7QzSE8/NX1N4z03zLF8Ln5a+ePEun+TJIDx81fD4+i4zufXYKreFjk5CFAyOKKcy5ZgaK+YcT3VJ2PlqDeJWNdr4JuN1yENcfGwkLlTxv/AErsPCtuF1CJlP1reT1sevJXPRJmVW2npUEo2tmp7pdy76rElmx3pxjzHnSmlsSRt+89q29JjAas+0sS5zjNdPY6asUee9epQjynDKpct26cVLLEGzUiKFUVKYiytivQITTVzPkswy5A5qk1u2CK6COHMfSo2t1EZ4GaBOSirnOvDiNj1FZNxCyxsysVPtXSNbbUORxVOSyDRlSDmoFfmMPTdSnsZlALEH3rrrTVDcKM8Y965e40+SHLBfpTVvprdlzlR3pxfczqK50eryG4GOtYOkzG1u2YrjnNaVjfx3i+p960J9Jg8tWBAPetefqVH4Tp9LvUkWPaa3IZPxritPha3yQ2F7VsW+oNtxnIrdTuediIe/dHURy84rShZFJy2K5e01ZY2+arTa4jdAKqMjHlkzrrVovI4IJqhqVxHbqWAxWH/bRSNggwaypdQuLh8SNlfSnKZSi1uTXWoedMfSoJJEK4J+arcFjFIN1MuY7WFdz8GuNu5UVZ3K8KMzZzxVxbdtv3c1ROtWkbbAuR7U2611lUeSlZnZBl/wCzhetRSeXjnArGXULibvtoljupl+/itQLl1cRKvJFYN9qcUeSPvVbksXaPks1Zt1pZ53JjFK4l7sjktevJdQV1b7lZWg6eYWOBkmuk1KzSFTz1p2i2fTAyTWVjq5jn9Y0+Zvnj4YVp+EdcnsW8ubls4rfktQsjbk5rKbT0+0F1GMmlciWp6ToWvONhLcH0r0bRtWjulGT0rxLR3MbY59q7zw7dhZMGuyjU6HnVo8yue16XOsy5BBrobct5eC3NcD4buscZyK7ezbdgk819Hh6icT5mrHldyr4iRn01yTyq8188+MFKzSfLu5zX0nqcYuLdl7EYr588eWDWd1Lzwa8jM4+7dHrZfU5tDzaYbXJFFFxw2KK/O5Sd9D7KMW0fJeks0iPtbPOa77wpuF8lcNpOk3KTAmNkHeu78LMYbwGT5QPWu7lcldno8+lz0KfJX2qGBQ0g75qvdatCiEBhxVvR2W8mVlFdNGB505I6bS7IJHvJrZWMrH1qvax/dTGFrQjg3Sf7NenCJwysOt0WRRjJq4sW2MgUW9vgnAwBVzy/lrezIukrFP7sfFVyu5uasSctgdKZwAR3FOxzVJXIWszIT6UjafntWlbNujPHJp272rJ3RvGWlzGuLAMvK1mzaGtx1XNdLJFuPBojtD1zTKbUjiG0WW2kIjO0VFN9tjcZJIFdy9jvJGeDTH0tWGCBigE0lY5eDU7lo9m01NFeXn9wgV1EOhw9QOKtjTY1hJAz+FOLOaXvPU5i3nuZJeRgVqLI4zg5xWittFFEZHXaB3qHTZYLuZlTFbRYbD7TzJMgjmmXFrPG3eugttPCYYjIq2bZJU+brV2bFI5O3kuo22g/LVqaxN0vzEmt1dNQtxzU9vYBXOegrNxsrmd0cjFpCq2Sua0X0tRjHH4V0n9npH0AahbfPVRUWQKVtjmf7L8tuF4qT7GVU55rZlUg4qrKu1TmnLQftUZs0Jjj44rIuCZVbcc1s3MhZSBWJcMPnxUXHGXM7nJ64omfavFa2h2JjhVqrW9mt5eEk10UMAhUKKR1La5Skg8xiTVY2qr8u2tpY1ZePvU6KIK2CtSyulyjDprLh16mug0G1c3QycfjUUMGOe3atjS7XNwCDVU1Y4ZySVj0Xw7YttjOOK7W1zwrda5zw2SsKY7V0luD5nSvpsKrxPmcVJXsW5MeWc9q8S+KmHkkOzmvbJP9W1eR/Ey3JnfvxmsMbFypG+B/i6Hh14iq2ScCip9ShZWJMZJ+lFfn06Wp9/HRHzn9s8lNpIqu1/J5mVOKx5Lwydeai+1Pu612xjpY0lorG/HeXM8ijfnNereCbOVbdXIJry/wPZtqWpKrZZa9z02AWECxBcV00vd3OGWpvWVmZ2BJrShhVW25yKzrSRux21PAsvmc16NON1c5pGzHGI8YPXrUrRhlqtDu8sE9RV62IK4aurlMLmfNGI24FQ+XvZiBWxcWwkXIPNVTb+TGTjmuexjLUggj8tTio5mYdKtQruVgKbND7VEkbR2sVI2OcmrkMwVagSJc4ZsGmHG/ANTYLlwZfnbxU0cPmdqhiVkBwGOBmpvPFvHmjlb2C5YW1WFAc1agjUxkcZPrWW8019MNo2oOtaTR/uXIP0xVKmx3KmtTQi1a3RcmsfQdJ8i8ZgCKuWNjJJeM8pLL71uWEYWV9uM1olYl6DsusY9qnSSOKEF25qQp94BawfEUdzIoEQKitkYSqG7b3EZb5cGpFnO4k1ieH7eWOMmXLH3rRExaQilLaxlzGnH89LMpjjyozVJLrb8lO+1MeOtZcohkyNszjmqkihky1Wprjt2qhcTqqECodmaKKKM0a881g3irGr4Na13KqR53c1nW9qb/AHqOSKyNow5diLSbFMl9tX5I8NxU9vamNMdKR1+bFBupJKwlvabo9w61YtrMyPlhin2KMvA6VqRQljmhrsP2i5R0dpFtA281oaTa+XJnFOtoB3UVs6da726V00qbZ5NSWtzo9FyvArqrZgy5FcxYW5jbJOK3bOT5gK+iw+iseVXipO7NCSIseOlee/ECOMs0m3JxivRDMAM8V5/8RsRWpPc0sRrSDDe7WsjyO8WOQklRRVeeTdJjNFfDzirn18ZyaPhc3WM/NSxyGRuGOcgfnWXCpZsKcluldT4Z8Pz6lqCk8ruGeKiN72PUqOx6h8J9BeC3aaQfMRkV6zb2BYBm5IrM8MaNFp9vHHt52811llaqWwAce9ehCJwcxHbxqrY2Gr8O/bnZzVmGGPvwauxxx54YYrvhFKOhjIyWuHiBypANTWU6yHBbFbRt4WUZAaqkmmwMcgeWKLs5ySPaFGGzmpZIFaMjqayv7MuVLNHIdq1BHqs1ir+YrNj2p2RmXYYSFJxgGo5WK9qoWPiqC5n8llx9a3riBbiEPH3rKzZpqYU21WLudorKm8S2kEuxSS9LrKz3TNbplfpWF/YP2d8ud0nrU+zZVjdtfFjyyBAm2PPJPpXTW/2e8hyrAt6V5xqF0ulxmR2GB2rT8M+JIbyMyFvLUUKLgS7o7GG6FvHKzDHoM1nXXiKZpFhiQ7D3xU0Cx3+/Em5K2bPQ0RI3CZXrk1p0uZ3Zc0mPbaq0vLNVjTrfy7h3PQ1PEoSPlBgU77ZBHCzNxRcxlcrwzP8AbpcnCdqlvYx5mCQRWcmpBrjCpuDUahcuyl87QOtVohRRbW4jjjYBlBrHkvcOxU81gSX+oXWqCCGItH3auss9ETaDIfn70l725XKZn26TOc1fs3kmP3qZdaasbZTpS20Zj6cVYuUnulKxkg8isC8mdVbJrptvmRt3rE1C1Xa1cvs2XE5q71AlSD2rW8Ll5LV3HDVQm0tWVieBW74ZhVbZsdKzcXHc6ouxYcFhnHFUHyJK3rhB5e1RkVkyW7K209KnVbmLTbuTWMnatu1z3rH0+DY1bluOK2p6h9k0beOtrTztbg1h27+1a+mtsbmuymmebUR0lnL5nWteHAcEdBWHasO1bFucN8xr2KbtscFQ0cB4ySK474i2Pn6TI/oM11qNmFqyvE1r9s0WdcZIWnWX7k56dTlrXR8z3lw0dy69x1oo8SRGxvp1IwAcUV8XUjaR9fSqJxPifS7Fpm3qpBr3D4a+GXtY1uJV3ZOeRXq2n/B/QtPbC2yuPpXQWfhu1shsijwg/hxWMZK9z0qzlJ2RnaRZAYcjGBg5rctYhGScYq1HDGqsAmC1Rm2dmyOldca0Tj5Z9iaO0SZsdKS/0xlhG1st7VEJJIZODWgt0doJ5IrX6ykrD5ZGFJLdaeyllLLWhDqSXkXzfKfSrF1/pC5IHPWqUNnGOcYb1o+sIPYs0rdo+g4B6jNU9ahiaFlVRzUkWI+gqVlWZsMOKX1pF+zR5yumz299v2fLXeaXOfsy5AAq1JYwTAgpVdbIL8oyB9aPrSMHTkjn9b1CKxmIRcyGslGluJmZlxurrptDikk3yDe3qRU66TCF+6M1k8WChLqjgr7w+2qP865Vfamah4dEeleVZp5Td8da9Kj02MxgKMetSNplv5ZymCe9YRxhq6Texx3gm0a3t2ikJc/3jXbx3ccMG18hQMYzVO30+K0YmNeD2qWa3U8PW6xatYj6u7XM+TVmedgGKr9ajeSa+cQQgyJ3IqzLo6TBmAq5p8P2FT5fyt61P1lEewbLFtYw6VZmRyDL2FYmsatHHlE+63WtWQm5bLcj0qu2jxyLl8Maf1pCVExNB137Re+SY9v+10rs7e33c/1rIt9Jt4JPMWPDVqRXBXjvVLFJGvsWOmtx0HNYOp6odPmx5efoK3GkduhwarS2cc8m+RAxo+uIPq5FZXwuEUqMbqbfWedrA5JqylqkLAp8uOlWIwjKQefSl9cXcPqpyl5ps0mQoOK0fD9m9tbkOuCa2ZCq9AKZHIrdWwaX1uJqsNbcqvM0e4gVGI/tClmGKuyGORSDTIlT5gDxUvExkDw76DLeFVNaNuBszVOPbzz0p8dyRxirjiI0zkdGSVjVhkVcZrVs5EVeTXMrcDIGaeNQKtgHiuiGMS3Od4ds7qzlDNww/Otq3zuyTuFeYrqkqrlHwakj8SXaHAlI/GuyOPgupy/UpS3PXbaQbWU4xTb7a1jNEMEsMV5K3iq+VWAmIFPPi67ZSxmOaqpmkJRsiIZbJyued/E61+zaiyhT97nFFdXfeXq02+4RZCxzyKK8KddSdz2IUHFWMlNwbGc1Ip+brRRXhQnJn0UYp6kowx54p5Xau3PFFFaczL5Y9ilJ/rOlSxx7VooqfaSM+VdhXYKuD1pkI39KKK052HKiwyhU561BEzM2BRRXN7SRn7KJajDDJIoiXc1FFaxqSZaikPkX3NJtOMUUU+dscoolhk8taY8xdsYooqTlSQ7acZoYjuM0UU1NnSopqwjOipgAg01WHlkZ5oorLnZm4pEA3L2qZZG20UVHtJGTS5rDvNK8mnqT1ooo55G/IrXF84etKZsDNFFLnZXKhpYnvRn0oorLnkTyojkkNJAQSaKKOdg4pjt2dwxSRSj5uKKKaqSQlFIesg2kEUwse1FFKNSXcrkQ3IRuWpoIaTrRRW3tJGXJEeGKr1qPcWPJxRRShOV7XJ5USqilWyagwd2McUUVsqkloWopO5Z3dABxRRRTUma8qP/Z";
    //"https://i.pinimg.com/originals/05/ac/17/05ac17fb09440e9071908ef00efef134.png";
    
    @ManyToMany()
    @JoinTable(
        name = "games_tagged",
        joinColumns = @JoinColumn(name = "game_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private final Set<Tag> tags = new HashSet<>();

    @ManyToMany()
    @JoinTable(
            name = "games_upvoted",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private final Set<User> upvotes = new HashSet<>();


    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    private final Set<Review> reviews = new HashSet<>();

    public Game() {}

    private Game(GameBuilder builder) {
        this.name = builder.name;
        this.owner = builder.owner;
        this.description = builder.description;
        this.releaseDate = builder.releaseDate;
        this.lastUpdate = builder.lastUpdate;
        this.cover = builder.cover;
    }

    public static GameBuilder create(String name) {
        return new GameBuilder(name);
    }

    public static class GameBuilder {
        private String description;
        private User owner;
        private final String name;
        private LocalDateTime releaseDate;
        private LocalDateTime lastUpdate;
        private String cover;

        public GameBuilder(String name) {
            this.name = name;
        }

        public GameBuilder description(String description) {
            this.description = description;
            return this;
        }

        public GameBuilder owner(User owner) {
            this.owner = owner;
            return this;
        }

        public GameBuilder releaseDate(LocalDateTime releaseDate) {
            this.releaseDate = releaseDate;
            return this;
        }

        public GameBuilder lastUpdate(LocalDateTime lastUpdate) {
            this.lastUpdate = lastUpdate;
            return this;
        }

        public GameBuilder cover(String cover) {
            this.cover = cover;
            return this;
        }

        public Game build() {
            if (description == null) {
                description = "";
            }
            if (cover == null) {
                cover = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAkACQAAD/2wBDAAIBAQIBAQICAgICAgICAwUDAwMDAwYEBAMFBwYHBwcGBwcICQsJCAgKCAcHCg0KCgsMDAwMBwkODw0MDgsMDAz/2wBDAQICAgMDAwYDAwYMCAcIDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAz/wAARCAAKAAoDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD9BbX/AILufBvVz8d9WtPjx+zzH4Z8D2cUHhIXep39vqc+orBMLkXlu8Qe5t/tAgET6cs5dDJ/FtByvhb/AMHN/wCx/dfDLw5J4w+OnhWHxdJpds2tpo/hzX205L4xKbgWxmsllMIl37DIqvt27gDkV/OF/wAF2PCel+B/+CvXx90vRdM0/R9MtvFMphtLG3S3gi3Rxu21EAUZZmY4HJYnqa/qH/ZI/wCCXH7MviT9lL4Y6jqP7OnwJ1DUNQ8J6Vc3V1c+AdKlmuZXs4meR3aAlmZiSWJJJJJoA//Z";
            }
            if (releaseDate == null) {
                releaseDate = LocalDateTime.now();
            }
            if (lastUpdate == null) {
                lastUpdate = LocalDateTime.now();
            }
            return new Game(this);
        }
    }
    
    // JSON //
    
    public static Game fromJson(String json) {
        final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, GsonAdapter.getLocalDateTimeAdapter()).create();
        return gson.fromJson(json, Game.class);
    }
    
    public JsonObject asJson() {
        JsonArray jsonArray = new JsonArray();
        for (Tag tag : tags) {
            jsonArray.add(tag.asJsonWithoutGames());
        }

        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("id", id);
        jsonObj.addProperty("name", name);
        jsonObj.addProperty("description", description);
        jsonObj.addProperty("owner_id", (owner == null ? null : owner.getId()));
        jsonObj.addProperty("cover", cover);
        jsonObj.addProperty("background_image", backgroundImage);
        jsonObj.addProperty("releaseDate", releaseDate.toString());
        jsonObj.addProperty("lastUpdate", lastUpdate.toString());
        jsonObj.add("tags", jsonArray);
        return jsonObj;
    }
    
    // RESTRICTIONS //

    /*
    public static Responses isGamePictureValid(String cover) {
        if (cover == null) {
            return new ErrorResponse(404, "Game picture cannot be null!");
        }
        if (cover.isEmpty()) {
            return new ErrorResponse(404, "Game picture cannot be empty!");
        }
        return new StatusResponse(200);
    }
    */
    
    public static Responses isNameValid(String name) {
        if (name == null) {
            return new ErrorResponse(404, "Name cannot be null!");
        }
        if (name.isEmpty()) {
            return new ErrorResponse(404, "Name cannot be empty!");
        }
        return new StatusResponse(200);
    }
    
    public static Responses isDescriptionValid(String description) {
        if (description == null) {
            return new ErrorResponse(404, "Description cannot be null!");
        }
        return new StatusResponse(200);
    }

    public static Responses isReleaseDateValid(LocalDateTime releaseDate) {
        if (releaseDate == null) {
            return new ErrorResponse(404, "Release date cannot be null!");
        }
        return new StatusResponse(200);
    }
    
    // ADDS? //
    
    protected void addInShelf(Shelf shelf) {
        inShelves.add(shelf);
    }
    
    protected void removeFromShelf(Shelf shelf) {
        inShelves.remove(shelf);
    }
    
    public void addTag(Tag tag) {
        tags.add(tag);
        tag.addGame(this);
    }
    
    public void addReview(Review review) {
        reviews.add(review);
        review.setGame(this);
    }

    public void addUpvote(User user) {
        upvotes.add(user);
        if (!user.getUpvotedGames().contains(this)) {
            user.addGameUpvote(this);
        }
     }
    
    // GETTERS - SETTERS //
    
    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getGamePicture() {
        return cover;
    }

    public void setGamePicture(String cover, LocalDateTime lastUpdate) {
        this.cover = cover;
        this.setLastUpdate(lastUpdate);
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name, LocalDateTime lastUpdate) {
        this.name = name;
        this.setLastUpdate(lastUpdate);
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description, LocalDateTime lastUpdate) {
        this.description = description;
        this.setLastUpdate(lastUpdate);
    }
    
    public LocalDateTime getReleaseDate() {
        return releaseDate;
    }
    
    public void setReleaseDate(LocalDateTime releaseDate, LocalDateTime lastUpdate) {
        this.releaseDate = releaseDate;
        this.setLastUpdate(lastUpdate);
    }
    
    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }
    
    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Set<Shelf> getInShelves() {
        return inShelves;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public Set<User> getUpvotes() {
        return upvotes;
    }

    // OTHERS //

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != Game.class) {
            return false;
        }
        return Objects.equals(this.id, ((Game) obj).id);
    }
}
