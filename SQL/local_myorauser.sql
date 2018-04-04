show user;

select *
from tab;

select *
from tbl_memo;

delete from tbl_memo
where no = 15;

commit;

insert into tbl_memo(no, name, msg)
values(seq_memo.nextval, '일순신','일순신입니다.');

insert into tbl_memo(no, name, msg)
values(seq_memo.nextval, '이순신','이순신입니다.');

insert into tbl_memo(no, name, msg)
values(seq_memo.nextval, '삼순신','삼순신입니다.');

insert into tbl_memo(no, name, msg)
values(seq_memo.nextval, '사순신','사순신입니다.');

insert into tbl_memo(no, name, msg)
values(seq_memo.nextval, '오순신','오순신입니다.');

insert into tbl_memo(no, name, msg)
values(seq_memo.nextval, '육순신','안녕하세요. 육순신입니다.');

insert into tbl_memo(no, name, msg)
values(seq_memo.nextval, '칠순신','안녕하세요. 칠순신입니다.');

commit;

select *
from tbl_memo
order by no desc;

drop table tbl_memo;
 
 select *
 from tbl_memo
 where msg like '%' || '안녕' || '%';

 commit;
 
 
 
 -- 제약조건을 추가
 
 select *
 from user_constraints A join user_cons_columns B
 on A.constraint_name = B.constraint_name
 where A.table_name = 'TBL_MEMO';
 
 
 alter table tbl_memo
 add constraint PK_tbl_memo_no primary key(no);
 
 alter table tbl_memo
 modify no not null;
 
 alter table tbl_memo
 modify name not null;
 
 alter table tbl_memo
 modify msg not null;
 
 -- Stored Procedure 생성하기 --
 
 create or replace procedure pcd_selectAllMemo
 ( o_date OUT SYS_REFCURSOR )
 is
 begin
     open o_date for 
     select no, name, msg AS MESSAGE
     from tbl_memo
     order by no desc;
 end pcd_selectAllMemo;
 
 
 create or replace procedure pcd_searchMemoByWord
 ( p_word IN varchar2,
   o_data OUT SYS_REFCURSOR )
 is
 begin
     open o_data for
     select no, name, msg
     from tbl_memo
     where msg like '%' || p_word || '%';
 end pcd_searchMemoByWord;
 
 
 create or replace procedure pcd_searchMemoByNo
 ( p_no IN tbl_memo.no%type,
   o_no OUT tbl_memo.no%type,
   o_name OUT tbl_memo.name%type,
   o_msg OUT tbl_memo.msg%type )
 is
 begin
     select no, name, msg
          into
              o_no, o_name, o_msg
     from tbl_memo
     where no = p_no;
 end pcd_searchMemoByNo;
 
 
create or replace procedure pcd_insertMemo
( p_name   IN   tbl_memo.name%type
	 ,p_msg    IN   tbl_memo.msg%type)
	 is
	    err_insert  exception;
	 begin
	      if (p_name = null OR p_msg = null) 
	         then raise err_insert;
	      end if;
	    
	      insert into tbl_memo(no, name, msg)
	      values(seq_memo.nextval, p_name, p_msg); 
	
	      exception
	         when err_insert then
	            raise_application_error(-20001, '작성자명과 글내용은 필수입력사항입니다.');
	 end pcd_insertMemo; 	
   

 create or replace procedure pcd_updateMemo
 ( p_no IN tbl_memo.no%type,
   p_name IN tbl_memo.name%type,
   p_msg IN tbl_memo.msg%type )
 is
 begin
     update tbl_memo set name = p_name, msg = p_msg
     where no = p_no;
 end pcd_updateMemo;
 
 
 create or replace procedure pcd_deleteMemo
 ( p_no IN tbl_memo.no%type )
 is
 begin
     delete from tbl_memo
     where no = p_no;
 end pcd_deleteMemo;
 
 
+"SELECT no, name, msg FROM tbl_memo ORDER BY no DESC"
 
+"SELECT no, name, msg FROM tbl_memo WHERE msg LIKE '%안녕%' ORDER BY no DESC";

+"SELECT no, name, msg FROM tbl_memo WHERE no = 10 ORDER BY no DESC";
 
 
 alter table tbl_memo
 add writeday date default sysdate;
 
 select no, name, msg, to_char(writeday, 'yyyy-mm-dd hh24:mi:ss')
 from tbl_memo
 order by no desc;
 
 
 update tbl_memo set writeday = writeday -30
 where no between 1 and 5;
 
  update tbl_memo set writeday = writeday -20
 where no between 6 and 7;
 
  update tbl_memo set writeday = writeday -25
 where no between 8 and 11;
 
  update tbl_memo set writeday = writeday -15
 where no between 12 and 18;
 
  update tbl_memo set writeday = writeday -12
 where no between 19 and 24;
 
   update tbl_memo set writeday = writeday -8
 where no between 24 and 100;
 
   update tbl_memo set writeday = sysdate
 where no between 101 and 1000;
 
 
 +"SELECT no, " 
+"  name, " 
+"  msg , " 
+"  TO_CHAR(writeday, 'yyyy-mm-dd hh24:mi:ss') AS WRITEDAY " 
+"FROM tbl_memo " 
+"WHERE to_date(TO_CHAR(sysdate, 'yyyy-mm-dd'), 'yyyy-mm-dd') - to_date(TO_CHAR(writeday, 'yyyy-mm-dd'), 'yyyy-mm-dd') <= 70 " 
+"ORDER BY no DESC;"
/*to_date(TO_CHAR(sysdate, 'yyyy-mm-dd'), 'yyyy-mm-dd') 
    - to_date(TO_CHAR(writeday, 'yyyy-mm-dd'), 'yyyy-mm-dd') <= 70 " 
      ==>현재날짜-작성한날짜 < 며
    
--to_char(sysdate, 'yyyy-mm-dd') 00시00분00초 
*/

--날짜구간검색 writeday between 시작날짜 and 종료날짜
+"SELECT no, " +"  name, " +"  msg, " +"  TO_CHAR(writeday, 'yyyy-mm-dd') AS writeday " +"FROM tbl_memo " +"WHERE TO_CHAR(writeday, 'yyyy-mm-dd') BETWEEN '2018-03-15' AND '2018-03-20'"
 
 
 
 --view생성
create or replace view view_memo
as
SELECT T.RNO, T.no, T.name, T.msg, T.writeday
FROM
(
SELECT rownum AS RNO, V.no, V.name, V.msg, v.writeday
FROM
( 
SELECT no, name, msg, to_char(writeday, 'yyyy-mm-dd hh24:mi:ss') AS WRITEDAY
FROM tbl_memo 
ORDER BY no
)V
)T 
ORDER BY T.RNO DESC;
 
 select *
 from view_memo;
 
 desc view_memo;
 
 ------------------------------------------------------------------------------------------------------------------------------------------------
 
 -- 관리자 로그인용
 
 create table tbl_admin
 (
  userid     varchar2(20)    not null,
  passwd     varchar2(20)    not null,
  name       varchar2(20)    not null,
  constraint PK_tbl_admin primary key(userid)
 );
 
 insert into tbl_admin(userid, passwd, name)
 values('admin', 'qwer1234$', '관리자');

-- 일반 사용자 로그인용
 
 create table tbl_member
 (
  userid     varchar2(20)    not null,
  passwd     varchar2(20)    not null,
  name       varchar2(20)    not null,
  constraint PK_tbl_member primary key(userid)
 ); 
 
 -- 일반사용자 상세정보
 create table tbl_member_detail
 (
   fk_userid     varchar2(20)   not null,
   birthday      varchar2(20)   not null,
   email         varchar2(100),
   tel           varchar2(20),
   address       varchar2(100),
   coin          number         default 0, -- 선불코인
   point         number         default 0, -- 포인트
   renttotal     number         default 0, -- 총 반납한 책의 양
   noreturn      number         default 0, -- 미반납 도서권수(미반납 책이 3권 이상이면 반납 불가)
   arear         number         default 0, -- 연체금
   constraint FK_tbl_member_detail_fk_userid
              foreign key(fk_userid) references tbl_member(userid)
              on delete cascade,
   constraint PK_tbl_member_detail primary key(fk_userid)
 );
 
 commit;
   
 select count(*)
 from tbl_admin
 where userid = 'admin' and passwd = 'qwer1234$';
 
 select count(*)
 from tbl_member
 where userid = 'qwer' and passwd = '1234';
 
 
 select *
 from tbl_member;
 
  select *
 from tbl_member_detail;
 
 create table tbl_bookcategory
 (
    categoryno      number        not null,
    categoryname    varchar2(100) not null,
    constraint PK_tbl_bookcategory primary key(categoryno)
 );
 
 
 create sequence seq_bookcategory
 start with 1000
 increment by 1000
 nomaxvalue
 nominvalue
 nocycle
 nocache;
 
 insert into tbl_bookcategory(categoryno, categoryname)
 values(seq_bookcategory.nextval, '소설');
 
 insert into tbl_bookcategory(categoryno, categoryname)
 values(seq_bookcategory.nextval, 'IT');
 
 insert into tbl_bookcategory(categoryno, categoryname)
 values(seq_bookcategory.nextval, '만화');
 
 commit;
 
 select *
 from tbl_bookcategory;
 
 
 create table tbl_book
 (
    bookcode        varchar2(50)    not null,
    fk_categoryno   number          not null,
    bookname        varchar2(100)   not null,
    publishday      date            not null,
    constraint PK_tbl_book primary key(bookcode),
    constraint FK_tbl_book_fk_categoryno
                 foreign key(fk_categoryno)
                 references tbl_bookcategory(categoryno)
 );
 
 select *
 from tbl_book;
  
 create table tbl_rentbook
 (
    rentbookno     number       not null,
    fk_bookcode    varchar2(50) not null,
    rentYN         number(1)  default 1 not null,  -- 대여중 0, 비치중 1
    constraint PK_tbl_rentbook primary key(rentbookno),
    constraint FK_tbl_rentbook_bookcode
                 foreign key(fk_bookcode)
                 references tbl_book(bookcode)
 );
 
 select *
 from tbl_rentbook;
 
 update tbl_rentbook set rentyn = 0
 where rentbookno = 2;
 
 commit;

 
 create sequence seq_rentbook
 start with 1
 increment by 1
 nomaxvalue
 nominvalue
 nocycle
 nocache;
 
+"SELECT a.categoryno, " 
+"  a.categoryname, " 
+"  b.cnt " 
+"FROM tbl_bookcategory A " 
+"JOIN " 
+"  ( SELECT fk_categoryno, COUNT(*) AS CNT FROM tbl_book GROUP BY fk_categoryno " 
+"  ) B " 
+"ON A.categoryno = B.fk_categoryno " 
+"ORDER BY 1"
 
 
 +"SELECT bookname, publishday FROM tbl_book WHERE fk_categoryno = 1000"
 
 
 select case when months_between(to_date(to_char(sysdate, 'yyyy-mm-dd'), 'yyyy-mm-dd'), publishday) < 6
        then '[신간]'
        else '[구간]'
        end AS OLDNEW,
        bookname, to_char(publishday, 'yyyy-mm-dd') AS publishday
 from tbl_book
 where fk_categoryno = 1000;
 
 
+"SELECT " 
+"  CASE " 
+"    WHEN months_between(to_date(TO_CHAR(sysdate, 'yyyy-mm-dd'), 'yyyy-mm-dd'), publishday) < 6 " 
+"    THEN '[신간]' " 
+"    ELSE '[구간]' " 
+"  END AS OLDNEW, " 
+"  bookname, " 
+"  TO_CHAR(publishday, 'yyyy-mm-dd') AS publishday " 
+"FROM tbl_book " 
+"WHERE fk_categoryno = 1000"

  
 select case when months_between(to_date(to_char(sysdate, 'yyyy-mm-dd'), 'yyyy-mm-dd'), publishday) < 6
        then '[신간]'
        else '[구간]'
        end AS OLDNEW,
        bookcode,
        bookname, to_char(publishday, 'yyyy-mm-dd') AS publishday
 from tbl_book
 where fk_categoryno = 1000;
 
 
 select fk_bookcode, count(*) AS TOTALCNT,
        sum(case rentyn when 1 then 1 else 0 end) AS EXISTSCNT,
        sum(case rentyn when 0 then 1 else 0 end) AS RENTINGCNT
 from tbl_rentbook
 group by fk_bookcode;
 
 -----------------------------------------------------------------------------------------------
 
 select A.OLDNEW, A.BOOKCODE, A.BOOKNAME, A.PUBLISHDAY,
        B.TOTALCNT, B.EXISTSCNT, B.RENTINGCNT
 from
 (
 select case when months_between(to_date(to_char(sysdate, 'yyyy-mm-dd'), 'yyyy-mm-dd'), publishday) < 6
        then '[신간]'
        else '[구간]'
        end AS OLDNEW,
        bookcode,
        bookname, to_char(publishday, 'yyyy-mm-dd') AS publishday
 from tbl_book
 where fk_categoryno = 3000 and adult != 1
 ) A join
 (
  select fk_bookcode, count(*) AS TOTALCNT,
        sum(case rentyn when 1 then 1 else 0 end) AS EXISTSCNT,
        sum(case rentyn when 0 then 1 else 0 end) AS RENTINGCNT
 from tbl_rentbook
 group by fk_bookcode
 ) B
 on A.bookcode = B.fk_bookcode
 order by 2;
 
 
+"SELECT A.OLDNEW, " 
+"  A.BOOKCODE, " 
+"  A.BOOKNAME, " 
+"  A.PUBLISHDAY, " 
+"  B.TOTALCNT, " 
+"  B.EXISTSCNT, " 
+"  B.RENTINGCNT " 
+"FROM " 
+"  (SELECT " 
+"    CASE " 
+"      WHEN months_between(to_date(TO_CHAR(sysdate, 'yyyy-mm-dd'), 'yyyy-mm-dd'), publishday) < 6 " 
+"      THEN '[신간]' " 
+"      ELSE '[구간]' " 
+"    END AS OLDNEW, " 
+"    bookcode, " 
+"    bookname, " 
+"    TO_CHAR(publishday, 'yyyy-mm-dd') AS publishday " 
+"  FROM tbl_book " 
+"  WHERE fk_categoryno = 3000 " 
+"  AND adult          != 1 " 
+"  ) A " 
+"JOIN " 
+"  (SELECT fk_bookcode, " 
+"    COUNT(*) AS TOTALCNT, " 
+"    SUM( " 
+"    CASE rentyn " 
+"      WHEN 1 " 
+"      THEN 1 " 
+"      ELSE 0 " 
+"    END) AS EXISTSCNT, " 
+"    SUM( " 
+"    CASE rentyn " 
+"      WHEN 0 " 
+"      THEN 1 " 
+"      ELSE 0 " 
+"    END) AS RENTINGCNT " 
+"  FROM tbl_rentbook " 
+"  GROUP BY fk_bookcode " 
+"  ) B " 
+"ON A.bookcode = B.fk_bookcode " 
+"ORDER BY 2"
 
 --- 성인물 유무 컬럼 추가하기 ---
 alter table tbl_book
 add adult number(1) default 0;   -- 성인물 : 1 / 전체관람 : 0
 
 
 select *
 from tbl_book;
 
 select *
 from tbl_rentbook;
 
 update tbl_book set adult = 1
 where bookcode = 'fiction0002';
 
 update tbl_book set adult = 1
 where bookcode = 'ani002';
 
 commit;
 
 
 select case when extract(year from sysdate) - to_number(substr(birthday, 1, 4)) < 20
        then 0 else 1 end AS adultCheck
 from tbl_member A join tbl_member_detail B
 ON A.userid = B.fk_userid
 where userid = 'catwoman' and passwd = 'qwer1234$'
 group by birthday


+"SELECT " 
+"  CASE " 
+"    WHEN extract(YEAR FROM sysdate) - to_number(SUBSTR(birthday, 1, 4)) < 20 " 
+"    THEN 0 " 
+"    ELSE 1 " 
+"  END AS adultCheck " 
+"FROM tbl_member A " 
+"JOIN tbl_member_detail B " 
+"ON A.userid  = B.fk_userid " 
+"WHERE userid = 'catwoman' " 
+"AND passwd   = 'qwer1234$' " 
+"GROUP BY birthday"

 create sequence seq_rentno
 start with 1
 increment by 1
 nomaxvalue
 nominvalue
 nocycle
 nocache;
 
 
 create table tbl_user_rentbook
 (
    seq_rentno        number       not null,
    fk_rentbookno     number       not null,
    fk_userid         varchar2(20) not null,
    rentday           date    default sysdate,
    constraint PK_tbl_tbl_user_rentbook primary key(seq_rentno),
    constraint FK_tbl_rentbook_fk_rentbookno
                 foreign key(fk_rentbookno)
                 references tbl_rentbook(rentbookno),
    constraint FK_tbl_user_rentbook_fk_userid
                 foreign key(fk_userid)
                 references tbl_member(userid)
                 on delete cascade
 );
 
 select *
 from tbl_rentbook;
 
 select *
 from tbl_book;
 
 create table tbl_user_returnbook
 (
    seq_rentno        number       not null,
    fk_rentbookno     number       not null,
    fk_userid         varchar2(20) not null,
    returnday         date    default sysdate,
    constraint PK_tbl_tbl_user_returnbook primary key(seq_rentno),
    constraint FKtblreturnbookfk_rentbookno
                 foreign key(fk_rentbookno)
                 references tbl_rentbook(rentbookno),
    constraint FK_tbl_returnbook_fk_userid
                 foreign key(fk_userid)
                 references tbl_member(userid)
                 on delete cascade
 );


 select *
 from tbl_user_rentbook;

 select *
 from tbl_user_returnbook;
 
 
 
 select A.OLDNEW, B.categoryname, A.bookname, c.rentyn
 from
 (
 select case when months_between(to_date(to_char(sysdate, 'yyyy-mm-dd'), 'yyyy-mm-dd'), publishday) < 6
        then '[신간]'
        else '[구간]'
        end AS OLDNEW,
        bookname,
        bookcode,
        fk_categoryno
 from tbl_book
 ) A join
 (
 select categoryno, categoryname
 from tbl_bookcategory
 ) B
 on A.fk_categoryno = B.categoryno
 join tbl_rentbook C
 on A.bookcode = C.fk_bookcode;

 
 
+"SELECT A.OLDNEW, " 
+"  B.categoryname, " 
+"  A.bookname, " 
+"  c.rentyn " 
+"FROM " 
+"  (SELECT " 
+"    CASE " 
+"      WHEN months_between(to_date(TO_CHAR(sysdate, 'yyyy-mm-dd'), 'yyyy-mm-dd'), publishday) < 6 " 
+"      THEN '[신간]' " 
+"      ELSE '[구간]' " 
+"    END AS OLDNEW, " 
+"    bookname, " 
+"    bookcode, " 
+"    fk_categoryno " 
+"  FROM tbl_book " 
+"  ) A " 
+"JOIN " 
+"  ( SELECT categoryno, categoryname FROM tbl_bookcategory " 
+"  ) B " 
+"ON A.fk_categoryno = B.categoryno " 
+"JOIN tbl_rentbook C " 
+"ON A.bookcode = C.fk_bookcode"
 
  
 select *
 from tbl_book;
 
 select *
 from tbl_rentbook;
  
 select *
 from tbl_bookcategory;
 
 
 
 
 