# final project 1단계 - 상품 구매 사이트

## 1. MySQL 설정 방법
+ username: root / password: ex)1234
  
![image](https://github.com/hyeonnnit/project-storeProduct/assets/153695703/19406ee0-f95c-4672-8ebb-44b05bce2de6)

+ 사용자 권한 부여 및 database 생성

![image](https://github.com/hyeonnnit/project-storeProduct/assets/153695703/b49c795c-2078-4626-9d67-c61e7dbf69d1)

## 2. 프로젝트 생성
+ 프로젝트 생성 설정
  
![image](https://github.com/hyeonnnit/project-storeProduct/assets/153695703/be011ba4-e67b-471d-a9b5-97ed4f8d1250)

+ 프로젝트 의존성 설정

![image](https://github.com/hyeonnnit/project-storeProduct/assets/153695703/4828ea7d-597d-4342-ab38-b914cf271364)

## 3. 기본 환경 설정
+ _core - 이미지 상대경로를 위한 생성
+ 필요 자바 클래스 생성
  
![image](https://github.com/hyeonnnit/project-storeProduct/assets/153695703/2a9feb9c-ae2e-4bdb-9691-9bcbb5701fa9)

+ application.yml 설정

![image](https://github.com/hyeonnnit/project-storeProduct/assets/153695703/b8358c08-9513-45a1-937c-1281a58d93f9)

+ build.gradle dekpendencies 설정

![image](https://github.com/hyeonnnit/project-storeProduct/assets/153695703/3c21f3c7-a8fc-401f-b358-13b9369d234f)

## 4. 구매자 기능 구현 시작
+ Query : JPQL
+ 핵심 기능 로직: 상품 -> 상품 목록, 상품 상세 정보 / 사용자 -> 회원가입, 로그인, 회원 수정 / 구매 -> 구매 목록, 구매하기 기능
+ 기능 구현 순서: Repository -> Request -> Response -> Service -> Controller -> mustache 수정
+ 데이터는 DTO로 변환해서 화면에 뿌리기위해 Response를 생성 
### 4-1. Product
+ github 주소 참고 - https://github.com/hyeonnnit/project-storeProduct

### 4-2. User
+ UserRepository - 쿼리를 이용해 데이터를 변환시키거나 가져올 수 있게 해준다.
```
@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final EntityManager em;

    public User updateById(int id, UserRequest.UpdateDTO reqDTO){
        User user = findById(id);
        user.setAddress(reqDTO.getAddress());
        user.setBirth(reqDTO.getBirth());
        user.setEmail(reqDTO.getEmail());
        user.setTel(reqDTO.getTel());
        user.setPassword(reqDTO.getPassword());
        return user;
    }

    public User findById(int id) {
        User user = em.find(User.class, id);
        return user;
    }

    public User findByUsernameAndPassword(String username, String password) {
        Query query = em.createQuery("select u from User u where u.username = :username and u.password = :password", User.class);
        query.setParameter("username", username);
        query.setParameter("password", password);
        return (User) query.getSingleResult();
    }

    public User save(User user) {
        em.persist(user);
        return user;
    }
}
```

+ UserRequest - 데이터를 새롭게 저장하거나 기존 데이터를 수정해서 저장할 때 데이터를 받아준다.
```
public class UserRequest {

    @Data
    public static class UpdateDTO{
        private String username;
        private String password;
        private String name;
        private String email;
        private String tel;
        private Date birth;
        private String address;
    }

    @Data
    public static class LoginDTO {
        private String username;
        private String password;
    }

    @Data
    public static class JoinDTO {
        private String username;
        private String password;
        private String name;
        private String email;
        private String tel;
        private Date birth;
        private String address;

        public User toEntity(){
            return User.builder()
                    .username(username)
                    .password(password)
                    .name(name)
                    .email(email)
                    .tel(tel)
                    .birth(birth)
                    .address(address)
                    .build();
        }
    }
}

```

+ UserResponse - 받아서 저장한 데이터를 화면에 전송하기 위해 사용한다.
```
public class UserResponse {
    @Data
    public static class UserDTO {
        private Integer id;
        private String username;
        private String password;
        private String name;
        private String email;
        private String tel;
        private Date birth;
        private String address;

        public UserDTO (User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.password = user.getPassword();
            this.name = user.getName();
            this.email = user.getEmail();
            this.tel = user.getTel();
            this.birth = user.getBirth();
            this.address = user.getAddress();
        }
    }
}

```

+ UserService - 쿼리를 작성한 Repository를 가져와 DTO에 넣어서 변환시킨다.
```
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public User editUser(int id, UserRequest.UpdateDTO reqDTO){
        return userRepository.updateById(id, reqDTO);
    }

    public User getUser(int id){
        User user = userRepository.findById(id);
        return user;
    }

    public User login(UserRequest.LoginDTO reqDTO){
        User user = userRepository.findByUsernameAndPassword(reqDTO.getUsername(), reqDTO.getPassword());
        return user;
    }

    @Transactional
    public UserResponse.UserDTO join(UserRequest.JoinDTO reqDTO) {
        User user = userRepository.save(reqDTO.toEntity());
        return new UserResponse.UserDTO(user);
    }
```

+ UserController - 작성한 코드들을 지정한 url과 view에 적용시키기 위해 Controller로 가져와서 전송한다.
```
@RequiredArgsConstructor
@Controller
public class UserController {
    private final UserService userService;
    private final HttpSession session;

    //로그인
    @PostMapping("/login")
    public String login(UserRequest.LoginDTO reqDTO) {
        User sessionUser = userService.login(reqDTO);
        session.setAttribute("sessionUser", sessionUser);
        return "redirect:/";
    }

    @GetMapping("/login-form")
    public String loginForm() {
        return "user/login-form";
    }

    //회원가입
    @PostMapping("/join")
    public String userJoin(UserRequest.JoinDTO reqDTO) {
        userService.join(reqDTO);
        return "redirect:/login-form";
    }

    @GetMapping("/join-form")
    public String userJoinForm() {
        return "user/join-form";
    }


    // 회원정보수정
    @PostMapping("/update")
    public String userUpdate(UserRequest.UpdateDTO reqDTO) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        User newSessionUser = userService.editUser(sessionUser.getId(), reqDTO);
        session.setAttribute("sessionUser",newSessionUser);
        return "redirect:/";
    }

    @GetMapping("/update-form")
    public String userUpdateForm(HttpServletRequest request) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        User user = userService.getUser(sessionUser.getId());
        request.setAttribute("user", user);
        return "user/update-form";
    }

    @GetMapping("/logout")
    public String logout() {
        session.invalidate();
        return "redirect:/";
    }
}
```

### 4-3. Order
+ OrderRepository
```
@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final EntityManager em;

    public Order save(Order order){
        em.persist(order);
        return order;
    }

    public Order findByProductId(int id) {
        Query query = em.createQuery("select o from Order o JOIN FETCH o.product p WHERE p.id =:id");
        query.setParameter("id", id);
        return (Order) query.getSingleResult();
    }

    public List<Order> findProductByUserId(int userId) {
        Query query =
                em.createQuery("select o from Order o JOIN FETCH o.product p JOIN FETCH o.user u WHERE u.id = :user_id", Order.class);
        query.setParameter("user_id", userId);
        return query.getResultList();
    }
}
```

+ OrderRequest
```
public class OrderRequest {

    @Data
    public static class SaveDTO{
        private Integer orderNum;
        private Integer priceSum;
        public Order toEntity(User user, Product product){
            return Order.builder()
                    .user(user)
                    .product(product)
                    .priceSum(priceSum)
                    .orderNum(orderNum)
                    .build();
        }
    }
}
```

+ OrderResponse
```
public class OrderResponse {
    @Data
    public static class DetailDTO{
        private int id;
        private User user;
        private Product product;
        private String name;
        private int price ;
        private int qty;
        private String pic;
        private Integer orderNum;
        private Integer priceSum;



        public DetailDTO(Order order){
            this.id = order.getProduct().getId();
            this.user=order.getUser();
            this.product=order.getProduct();
            this.orderNum = order.getOrderNum();
            this.priceSum = order.getPriceSum();
            this.name = order.getProduct().getName();
            this.price = order.getProduct().getPrice();
            this.qty = order.getProduct().getQty();
            this.pic = order.getProduct().getPic();
        }
    }

    @Data
    public static class ListDTO{
        private int id;
        private String name;
        private int price ;
        private int qty;
        private String pic;
        private Integer orderNum;
        private Integer priceSum;

        public ListDTO(Order order){
            this.id = order.getProduct().getId();
            this.orderNum = order.getOrderNum();
            this.priceSum = order.getPriceSum();
            this.name = order.getProduct().getName();
            this.price = order.getProduct().getPrice();
            this.qty = order.getProduct().getQty();
            this.pic = order.getProduct().getPic();
        }
    }
}
```

+ OrderService
```
@RequiredArgsConstructor
@Service
public class OrderService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public OrderResponse.DetailDTO orderSaveProduct(Integer productId, User user, OrderRequest.SaveDTO reqDTO){
        Product product = productRepository.findById(productId);
        if (product.getQty() < reqDTO.getOrderNum()) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }
        product.setQty(product.getQty() - reqDTO.getOrderNum());
        productRepository.update(product);
        Order order = orderRepository.save(reqDTO.toEntity(user, product));
        order.setPriceSum(product.getPrice()*reqDTO.getOrderNum());
        return new OrderResponse.DetailDTO(order);
    }

    public OrderResponse.DetailDTO getOrderDetail(int id){
        Order order = orderRepository.findByProductId(id);
        return new OrderResponse.DetailDTO(order);
    }

    public List<OrderResponse.ListDTO> getOrderList(int userId){
        List<Order> orderList = orderRepository.findProductByUserId(userId);
        return orderList.stream().map(OrderResponse.ListDTO::new).collect(Collectors.toList());
    }
}
```

+ OrderController
```
@RequiredArgsConstructor
@Controller
public class OrderController {
    private final OrderService orderService;
    private final HttpSession session;

    @GetMapping("/orders")
    public String orderList(HttpServletRequest request) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        List<OrderResponse.ListDTO> orderList = orderService.getOrderList(sessionUser.getId());
        request.setAttribute("orderList", orderList);
        return "order/product-list";
    }

    @GetMapping("/order/{id}/product-form")
    public String orderForm(@PathVariable Integer id, HttpServletRequest request){
        OrderResponse.DetailDTO order = orderService.getOrderDetail(id);
        request.setAttribute("order", order);
        return "order/product-order-form";
    }

    @PostMapping("/order/{id}/product")
    public String order(@PathVariable Integer id, OrderRequest.SaveDTO reqDTO){
        User sessionUser = (User) session.getAttribute("sessionUser");
        orderService.orderSaveProduct(id, sessionUser, reqDTO);
        return "redirect:/orders";
    }
}
```

## 5. View - mustache 사용
+ Controller에서 지정한 변수를 적용하여 화면에 전송시켜준다.

### 5-1. layout
+ layout/header.mustache
```
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Product</title>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <link
            href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css"
            rel="stylesheet"/>
    <script
            src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script
            src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
    <link
            href="https://cdn.jsdelivr.net/npm/summernote@0.8.18/dist/summernote-lite.min.css"
            rel="stylesheet"/>
    <script
            src="https://cdn.jsdelivr.net/npm/summernote@0.8.18/dist/summernote-lite.min.js"></script>
    <link
            href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.0/css/all.min.css"
            rel="stylesheet"/>
    <link href="/css/style.css" rel="stylesheet"/>
</head>
<body>
<nav class="navbar navbar-expand-sm bg-dark navbar-dark">
    <div class="container-fluid">
        <a class="navbar-brand" href="/">Product</a>
        <button class="navbar-toggler" type="button"
                data-bs-toggle="collapse" data-bs-target="#collapsibleNavbar">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="collapsibleNavbar">
            <ul class="navbar-nav">
                {{#sessionUser}}
                    <li class="nav-item"><a class="nav-link" href="/update-form">회원정보수정</a>
                    </li>
                    <li class="nav-item"><a class="nav-link" href="/">상품목록</a>
                    </li>
                    <li class="nav-item"><a class="nav-link" href="/orders">구매내역</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="/logout">로그아웃</a>
                    </li>
                {{/sessionUser}}
                {{^sessionUser}}
                    <li class="nav-item"><a class="nav-link" href="/join-form">회원가입</a>
                    </li>
                    <li class="nav-item"><a class="nav-link" href="/login-form">로그인</a>
                    </li>
                {{/sessionUser}}


            </ul>
        </div>
    </div>
</nav>
```

+ layout/footer.mustache
```
<div class="ft-in pt-40 pb-40 ft-16 bg-dark navbar-dark">
    <div class="d-flex">
        <div class="ft-info-w" style="margin-top: 30px; color: darkgrey;">
            <ul class="ft-info d-flex" style="list-style-type: none;">
                <li class="mr-20" style="margin-right: 10px;">(주)스토어</li>
                <li class="mr-20" style="margin-right: 10px;">대표 : 한종희</li>
                <li class="mr-20" style="margin-right: 10px;">개인정보보호책임자 : 이준희</li>
                <li class="mr-20" style="margin-right: 10px;">사업자등록번호 : 124-81-00998</li>
                <li class="mr-20" style="margin-right: 10px;">직업정보제공사업 신고번호 : 02-2255-0114</li>
            </ul>
            <ul style="list-style-type: none;">
                <li style="margin-bottom: 10px">경기도 수원시 영통구 삼성로129(메탄동)</li>
                <li style="color:grey;">본 사이트는 상업적으로 사용하지 않습니다.</li>
            </ul>
        </div>
    </div>
</div>
```

### 5-2. product
+ product/detail.mustache
```
{{> layout/header}}
<div class="d-flex justify-content-center" style="margin-top: 100px; margin-bottom: 200px;">
    {{#product}}
        <div class="p-3 m-3" style="width: 300px;">
            <img src="/images/{{pic}}" width="300" height="300">
        </div>

        <div class="p-3 m-3" style="width: 300px;">
            <div class="mb-3 mt-3">
                상 품 명 : <input name="name" type="text" class="form-control" value="{{name}}" readonly>
            </div>
            <div class="mb-3 mt-3">
                상품가격 : <input name="price" type="number" class="form-control" value="{{price}}" readonly>
            </div>
            <div class="mb-3 mt-3">
                상품수량 : <input name="qty" type="number" class="form-control" value="{{qty}}" readonly>
            </div>
            <div class="d-flex justify-content-center">
                <span><form action="/order/{{product.id}}/product-form">
                    <button class="btn btn-danger mt-3">구매하기</button>
                </form>
                </span>
            </div>
        </div>


    {{/product}}

</div>
{{> layout/footer}}
```

+ product/list.mustache
```
{{> layout/header}}

<div class="container">
    <table class="table table-hover offer-table scroll" style="text-align: center; border-top:2px solid #ddd">
        <br>
        <h2>상품 목록</h2>
        <br>

        <thead>
        <tr>
            <th class="center-align col-1">No</th>
            <th class="center-align col-5">상품명</th>
            <th class="center-align col-2">상품가격</th>
            <th class="center-align col-2">상품수량</th>
            <th class="center-align col-2">상세보기</th>
        </tr>
        </thead>
        {{#productList}}
            <tbody>
            <tr class="offer-table">
                <th scope="row">{{id}}</th>
                <td>{{name}}</td>
                <td>{{price}}</td>
                <td>{{qty}}</td>
                <td>
                    <div class="new-create-button">
                        <form action="/product/{{id}}">
                            <button type="submit" class="btn btn-outline-primary">상세보기</button>
                        </form>
                    </div>
                </td>
            </tr>
            </tbody>
        {{/productList}}
    </table>
</div>

<div style="margin-bottom: 25%"></div>
{{> layout/footer}}
```

### 5-3. User
+ user/join-form.mustache
```
{{> layout/header}}

<head>
    <style>
        .bg-light {
            height: 1053px;
            padding-top: 55px;
            padding-bottom: 75px;
        }

        .flex-fill.mx-xl-5.mb-2 {
            margin: 0 auto;
            width: 700px;
            padding-right: 7rem;
            padding-left: 7rem;
        }

        .container.py-4 {
            margin: 0 auto;
            width: 503px;
        }

        .d-grid.gap-2 {
            padding-top: 30px;
        }

        .bir_yy, .bir_mm, .bir_dd {
            width: 160px;
            display: table-cell;
        }

        .bir_mm + .bir_dd, .bir_yy + .bir_mm {
            padding-left: 10px;
        }
    </style>
</head>


<section class="bg-light">
    <div class="container py-4">
        <ul class="nav nav-tabs nav-justified">
            <li class="nav-item fs-4">
                <a class="nav-link user fw-600">개인 회원가입</a>
            </li>
        </ul>

        <!--        폼 시작 -->
        <form action="/join" method="post">
            <div class="mb-3 mt-3 form-group">
                <input type="hidden" name="role" value="1">

                아이디 <input id="username" name="username" type="text" class="form-control" placeholder="아이디를 입력하세요">
                <!--                <button type="button" class="btn btn-primary mt-3" onclick="">중복확인</button>-->
                <div class="alert" id="passCheck"></div>
            </div>

            <!--            비밀번호 -->
            <div class="mb-3 mt-3 has-success form-group">
                비밀번호 <input id="password" name="password" type="password" class="form-control" placeholder="비밀번호를 입력하세요">
            </div>

            <div class="mb-3 mt-5 has-danger form-group">
                비밀번호 확인 <input id="passwordCheck" name="passwordCheck" type="password" class="form-control" placeholder="비밀번호를 다시 입력하세요">
                <div class="alert" id="passCheck"></div>
            </div>
            <!--            비밀번호 끝-->

            <!--            본명 -->
            <div class="mb-3 mt-3 form-group">
                이름 <input id="name" name="name" type="text" class="form-control" placeholder="이름을 입력하세요">
            </div>
            <!--            본명 끝 -->

            <!--            핸드폰 -->
            <div class="mb-3 mt-5 form-group">
                전화번호 <input id="tel" name="tel" type="text" class="form-control" placeholder="전화번호를 입력하세요">
            </div>
            <!--            핸드폰 -->

            <!--            생년월일 -->
            <div class="mb-3 mt-5 form-group">
                생년월일 <input id="birth" name="birth" type="date" class="form-control" placeholder="전화번호를 입력하세요">
            </div>


            <!--이메일-->
            <div class="mb-3 mt-5">
                이메일 <input id="email" name="email" type="email" class="form-control" placeholder="이메일을 입력하세요">
            </div>
            <!-- 이메일-->

            <!--주소-->
            <div class="mb-3 mt-5 form-group">
                주소 <input id="address" name="address" type="text" class="form-control" placeholder="주소를 입력하세요">
            </div>
            <!-- 이메일-->

            <!-- 가입하기 버튼-->
            <div class="d-flex justify-content-center">
                <button class="btn btn-primary btn-lg" type="submit">가입하기</button>
            </div>
            <!--            가입하기 버튼 끝-->

        </form>
        <!--        폼 끝 -->

    </div>
</section>

{{> layout/footer}}
```

+ user/login-form.mustache
```
{{> layout/header}}

<head>
    <style>
        .bg-light {
            height: 1053px;
            padding-top: 55px;
            padding-bottom: 75px;
        }

        .flex-fill.mx-xl-5.mb-2 {
            margin: 0 auto;
            width: 700px;
            padding-right: 7rem;
            padding-left: 7rem;
        }

        .container.py-4 {
            margin: 0 auto;
            width: 503px;
        }

        .d-grid.gap-2 {
            padding-top: 30px;
        }

        .bir_yy, .bir_mm, .bir_dd {
            width: 160px;
            display: table-cell;
        }

        .bir_mm + .bir_dd, .bir_yy + .bir_mm {
            padding-left: 10px;
        }
    </style>
</head>


<section class="bg-light">
    <div class="container py-4">
        <ul class="nav nav-tabs nav-justified">
            <li class="nav-item fs-4">
                <a class="nav-link user fw-600">로그인</a>
            </li>
        </ul>

        <!--        폼 시작 -->
        <form action="/login" method="post">
            <div class="mb-3 mt-3 form-group">
                아이디 <input id="username" name="username" type="text" class="form-control" placeholder="아이디를 입력하세요" value="ssar">
            </div>

            <!--            비밀번호 -->
            <div class="form-group mb-3 mt-3 has-success">
                비밀번호 <input id="password" name="password" type="password" class="form-control" placeholder="비밀번호를 입력하세요" value="1234">
            </div>

            <div class="mb-3 mt-3 form-group">
                <div class="custom-checkbox custom-control">
                    <input type="checkbox" name="remember" id="remember" class="custom-control-input">
                    <label for="remember" class="custom-control-label">아이디 기억하기</label>
                </div>
            </div>


            <!-- 로그인 버튼-->
            <div class="d-grid gap-2">
                <button class="btn btn-primary btn-lg" type="submit">로그인</button>
            </div>
            <!--            로그인 버튼 끝-->

        </form>
        <!--        폼 끝 -->

    </div>
</section>

{{> layout/footer}}
```

+ user/update-form.mustache
```
{{> layout/header}}

<head>
    <style>
        .bg-light {
            height: 1053px;
            padding-top: 55px;
            padding-bottom: 75px;
        }

        .flex-fill.mx-xl-5.mb-2 {
            margin: 0 auto;
            width: 700px;
            padding-right: 7rem;
            padding-left: 7rem;
        }

        .container.py-4 {
            margin: 0 auto;
            width: 503px;
        }

        .d-grid.gap-2 {
            padding-top: 30px;
        }

        .bir_yy, .bir_mm, .bir_dd {
            width: 160px;
            display: table-cell;
        }

        .bir_mm + .bir_dd, .bir_yy + .bir_mm {
            padding-left: 10px;
        }
    </style>
</head>


<section class="bg-light">
    <div class="container py-4">
        <ul class="nav nav-tabs nav-justified">
            <li class="nav-item fs-4">
                <a class="nav-link user fw-600">회원정보 수정</a>
            </li>
        </ul>

        <!--        폼 시작 -->
        <form action="/update" method="post">

            <div class="mb-3 mt-3 form-group">
                아이디 <input id="username" name="username" type="text" class="form-control" value="{{user.username}}" disabled>
                <!--                <button type="button" class="btn btn-primary mt-3" onclick="">중복확인</button>-->
                <div class="alert" id="passCheck"></div>
            </div>

            <!--            비밀번호 -->
            <div class="mb-3 mt-3 has-success form-group">
                비밀번호 <input id="password" name="password" type="password" class="form-control" value="{{user.password}}">
            </div>

            <div class="mb-3 mt-5 has-danger form-group">
                비밀번호 확인 <input id="passwordCheck" name="passwordCheck" type="password" class="form-control" placeholder="비밀번호를 다시 입력하세요">
                <div class="alert" id="passCheck"></div>
            </div>
            <!--            비밀번호 끝-->

            <!--            본명 -->
            <div class="mb-3 mt-3 form-group">
                이름 <input id="name" name="name" type="text" class="form-control" value="{{user.name}}" disabled>
            </div>
            <!--            본명 끝 -->

            <!--            핸드폰 -->
            <div class="mb-3 mt-5 form-group">
                전화번호 <input id="tel" name="tel" type="text" class="form-control" value="{{user.tel}}">
            </div>
            <!--            핸드폰 -->

            <div class="mb-3 mt-5 form-group">
                생년월일 <input id="birth" name="birth" type="date" class="form-control" value="{{user.birth}}">
            </div>
            <!--이메일-->
            <div class="mb-3 mt-5">
                이메일 <input id="email" name="email" type="email" class="form-control" value="{{user.email}}">
            </div>
            <!-- 이메일-->

            <!--주소-->
            <div class="mb-3 mt-5 form-group">
                주소 <input id="address" name="address" type="text" class="form-control" value="{{user.address}}">
            </div>

            <div class="d-flex justify-content-center">
                <button class="btn btn-primary " type="submit">수정완료</button>
            </div>

        </form>
        <!--        폼 끝 -->

    </div>
</section>
{{> layout/footer}}
```

### 5-4. Order
+ order/product-list.mustache
```
{{> layout/header}}

<div class="container">

    <br>
    <ul class="row row-cols-4 gap-3">
        {{#orderList}}
            <li class="card col border p-5 rounded m-3" style="width: 15rem; padding: 0px !important; border: 0">
                <div style="width: 100%; height: 15rem; overflow: hidden">
                    <img src="/images/{{pic}}" class="card-img-top"/>
                </div>

                <div class="card-body">
                    <h5 class="card-title" style="
                            display: -webkit-box;
                            -webkit-line-clamp: 1;
                            -webkit-box-orient: vertical;
                            overflow: hidden;
                            text-overflow: ellipsis;
                          "><b>{{name}}</b></h5>
                    <h6><span>구매 금액</span> | <span>{{priceSum}}원</span></h6>
                    <h6><span>구매수량</span> | <span>{{orderNum}}</span></h6>
                </div>
            </li>
        {{/orderList}}


    </ul>
</div>

<div style="margin-bottom: 25%"></div>
{{> layout/footer}}

```

+ order/product-order-form.mustache
```
{{> layout/header}}
<form action="/order/{{id}}/product" method="post">
    <div class="d-flex justify-content-center" style="margin-top: 100px; margin-bottom: 200px;">
        <div class="p-3 m-3" style="width: 300px;">
            <img src="/images/{{order.pic}}" width="300" height="300">
        </div>
        <div class="p-3 m-3" style="width: 300px;">
            <div class="mb-3 mt-3">
                상 품 명 : <input name="name" type="text" class="form-control" value="{{order.name}}" readonly>
            </div>
            <div class="mb-3 mt-3">
                상품가격 : <input name="price" type="number" class="form-control" value="{{order.price}}" readonly>
            </div>
            <div class="mb-3 mt-3">
                상품수량 : <input name="qty" type="number" class="form-control" value="{{order.qty}}" readonly>
            </div>
            <div class="mb-3 mt-3">
                구매수량 : <input name="orderNum" type="number" class="form-control" >
            </div>
            <div class="d-flex justify-content-center">
                <button type="submit" class="btn btn-danger mt-3">구매 완료</button>
            </div>
        </div>
    </div>
</form>
{{> layout/footer}}
```
