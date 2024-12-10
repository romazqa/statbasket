--
-- PostgreSQL database dump
--

-- Dumped from database version 16.2
-- Dumped by pg_dump version 16.2

-- Started on 2024-12-10 13:47:06

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 215 (class 1259 OID 16546)
-- Name: eventt; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.eventt (
    id_event integer NOT NULL,
    name_event character varying(40),
    locationn character varying(40),
    year_event integer,
    id_competition_level integer NOT NULL
);


ALTER TABLE public.eventt OWNER TO postgres;

--
-- TOC entry 216 (class 1259 OID 16549)
-- Name: level_event; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.level_event (
    id_competition_level integer NOT NULL,
    competition_level character varying(30)
);


ALTER TABLE public.level_event OWNER TO postgres;

--
-- TOC entry 217 (class 1259 OID 16552)
-- Name: eventt_v; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.eventt_v AS
 SELECT eventt.id_event,
    eventt.name_event,
    eventt.locationn,
    eventt.year_event,
    eventt.id_competition_level,
    level_event.competition_level
   FROM (public.eventt
     JOIN public.level_event ON ((eventt.id_competition_level = level_event.id_competition_level)));


ALTER VIEW public.eventt_v OWNER TO postgres;

--
-- TOC entry 218 (class 1259 OID 16556)
-- Name: m_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.m_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.m_seq OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 16557)
-- Name: matches; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.matches (
    id_matches integer DEFAULT nextval('public.m_seq'::regclass) NOT NULL,
    date_matches date NOT NULL,
    team1score integer NOT NULL,
    team2score integer NOT NULL,
    id_event integer NOT NULL,
    id_team1 integer NOT NULL,
    id_team2 integer NOT NULL,
    playground character varying(40) NOT NULL
);


ALTER TABLE public.matches OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 16561)
-- Name: team; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.team (
    id_team integer NOT NULL,
    city character varying(50) NOT NULL,
    team_name character varying(50) NOT NULL,
    gender_team character varying(5) NOT NULL
);


ALTER TABLE public.team OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 16564)
-- Name: matches_v; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.matches_v AS
 SELECT matches.id_matches,
    matches.date_matches,
    matches.team1score,
    matches.team2score,
    eventt.id_event,
    eventt.name_event,
    matches.id_team1,
    team1.team_name,
    matches.id_team2,
    team2.team_name AS team_name2,
    matches.playground
   FROM (((public.matches
     JOIN public.team team1 ON ((matches.id_team1 = team1.id_team)))
     JOIN public.team team2 ON ((matches.id_team2 = team2.id_team)))
     JOIN public.eventt ON ((matches.id_event = eventt.id_event)));


ALTER VIEW public.matches_v OWNER TO postgres;

--
-- TOC entry 222 (class 1259 OID 16569)
-- Name: pl_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.pl_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.pl_seq OWNER TO postgres;

--
-- TOC entry 223 (class 1259 OID 16570)
-- Name: player; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.player (
    id_player integer DEFAULT nextval('public.pl_seq'::regclass) NOT NULL,
    birthday date NOT NULL,
    player_name character varying(40) NOT NULL,
    height numeric(3,0) NOT NULL,
    weight numeric(3,0) NOT NULL,
    role character varying(30) NOT NULL,
    id_team integer NOT NULL,
    game_number numeric(3,0) NOT NULL,
    gender character varying(5) NOT NULL
);


ALTER TABLE public.player OWNER TO postgres;

--
-- TOC entry 224 (class 1259 OID 16574)
-- Name: player_stats; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.player_stats (
    id_playerstats integer DEFAULT nextval('public.m_seq'::regclass) NOT NULL,
    pointscored integer,
    assists integer,
    steal integer,
    turnover integer,
    blocked_shot integer,
    foul integer,
    double integer,
    triple integer,
    free_throw integer,
    defensive_rebound integer,
    offensive_rebound integer,
    id_player integer NOT NULL,
    id_matches integer NOT NULL
);


ALTER TABLE public.player_stats OWNER TO postgres;

--
-- TOC entry 225 (class 1259 OID 16578)
-- Name: player_v; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.player_v AS
 SELECT player.id_player,
    player.birthday,
    player.player_name,
    player.height,
    player.weight,
    player.role,
    team.id_team,
    team.team_name,
    player.game_number,
    player.gender
   FROM (public.player
     JOIN public.team ON ((team.id_team = player.id_team)));


ALTER VIEW public.player_v OWNER TO postgres;

--
-- TOC entry 226 (class 1259 OID 16582)
-- Name: ps_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.ps_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.ps_seq OWNER TO postgres;

--
-- TOC entry 227 (class 1259 OID 16583)
-- Name: ps_v; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.ps_v AS
 SELECT ps.id_playerstats,
    ps.pointscored,
    ps.assists,
    ps.steal,
    ps.turnover,
    ps.blocked_shot,
    ps.foul,
    ps.double,
    ps.triple,
    ps.free_throw,
    ps.defensive_rebound,
    ps.offensive_rebound,
    pl.id_player,
    pl.player_name,
    ps.id_matches
   FROM (public.player_stats ps
     JOIN public.player pl ON ((pl.id_player = ps.id_player)));


ALTER VIEW public.ps_v OWNER TO postgres;

--
-- TOC entry 4847 (class 0 OID 16546)
-- Dependencies: 215
-- Data for Name: eventt; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.eventt (id_event, name_event, locationn, year_event, id_competition_level) FROM stdin;
1	МССИ	МОСКВА	2023	1
2	МССИ	МОСКВА	2023	2
3	МССИ	МОСКВА	2023	3
5	Студенческая лига РЖД	Санкт-Петербург	2024	4
4	Кубок Гомельского	Красноярск	2024	4
6	Моспром	Арена Баскетбола	2024	5
7	Соревнование1	Спортзал	2024	4
\.


--
-- TOC entry 4848 (class 0 OID 16549)
-- Dependencies: 216
-- Data for Name: level_event; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.level_event (id_competition_level, competition_level) FROM stdin;
2	Дивизион Б
4	Всероссийские соревнования
1	Дивизион А
3	Дивизион В
5	Городские соревнования
6	Любительский Уровень
7	Высшая лига
\.


--
-- TOC entry 4850 (class 0 OID 16557)
-- Dependencies: 219
-- Data for Name: matches; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.matches (id_matches, date_matches, team1score, team2score, id_event, id_team1, id_team2, playground) FROM stdin;
6	2024-04-01	45	69	2	3	1	РЭУ 
7	2024-04-05	52	42	1	5	4	РГАУ-МСХА
4	2024-03-21	90	75	3	4	6	РЭУ
5	2024-03-25	92	84	4	5	6	РЭУ 
3	2024-02-16	62	58	2	4	5	РГУ им. А.Н.Косыгина 
1	2024-02-03	58	81	1	2	3	РГАУ-МСХА
2	2024-02-13	45	71	1	2	1	РГУ им. А.Н.Косыгина 
9	2024-05-02	60	70	1	1	3	РГУ им.А.Н.Косыгина
76	2024-06-25	53	41	4	1	2	РГУ
77	2024-09-24	10	0	2	4	5	РГУ
78	2024-11-03	8	6	2	1	2	РГУ
79	2024-11-04	5	11	5	4	5	РЭУ
\.


--
-- TOC entry 4853 (class 0 OID 16570)
-- Dependencies: 223
-- Data for Name: player; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.player (id_player, birthday, player_name, height, weight, role, id_team, game_number, gender) FROM stdin;
1	2001-08-23	Зубакова Елена	167	57	Разыгрывающий	1	2	Ж
3	2001-11-08	Кудрявцева Екатерина	180	60	Центровой	1	11	Ж
6	2001-06-07	Мартиросянц Евгения	173	60	Центровой	1	15	Ж
7	2002-04-28	Мельникова Екатерина	168	66	Тяжелый Форвард	1	27	Ж
8	2004-04-27	Бахлынова Мария	178	63	Легкий форвард	1	27	Ж
9	2003-01-07	Вдовыко Алина	165	58	Разыгрывающий	1	35	Ж
10	2002-04-08	Боркова Ангелина	180	70	Тяжелый Форвард	1	77	Ж
11	2008-02-12	Моцная Ирина	169	77	тяжелый форвард	2	1	Ж
12	2003-01-03	Бобырева Софья	175	62	Атакующий защитник	2	3	Ж
13	2003-10-21	Кузнецова Светлана	182	69	Легкий форвард	2	4	Ж
14	1999-03-02	Кузнецова Елена	172	54	Легкий форвард	2	10	Ж
15	2001-12-17	Смык Ксения	181	66	Тяжелый форвард	2	12	Ж
16	2002-01-28	Чванкина Юлия	170	58	Легкий форвард	2	13	Ж
17	2004-03-20	Закаридзе Анна	181	74	Центровой	2	14	Ж
18	2004-04-04	Павлова Ксения	184	75	Центровой	2	15	Ж
19	2002-01-04	Ванда Анастасия	169	64	Легкий форвард	2	17	Ж
20	2005-10-01	Богомолова Елена	167	55	Разыгрывающий	2	19	Ж
21	2003-02-15	Елисенко Виктория	160	55	Разыгрывающий	2	18	Ж
22	2003-09-25	Дроздова Евгения	173	64	Легкий Форвард	2	20	Ж
23	2004-07-20	Степанова Мария	174	60	Атакующий защитник	2	22	Ж
24	2000-02-13	Янькова Анастасия	160	65	Легкий форвард	2	23	Ж
25	2005-04-20	Хомутова Виктория	170	44	Разыгрывающий	2	21	Ж
2	2004-02-22	Кравченко Валерия	172	58	Атакующий защитник	1	10	Ж
4	2002-01-04	Хорошавина Анна	170	64	Легкий форвард	1	12	Ж
5	2005-10-13	Терещенко Вероника	183	79	Центровой	1	13	Ж
26	2000-01-06	Мартыненко Алина	158	47	Разыгрывающий	3	0	Ж
27	2003-11-03	Махалина Кристина	180	65	Разыгрывающий	3	1	Ж
28	2005-01-09	Зеленова Мария	171	53	Атакующий защитник	3	4	Ж
29	2001-01-09	Толстопят Дииана	183	65	Тяжелый форвард	3	5	Ж
30	2001-08-02	Коляда Анастасия	180	55	Легкий форвард	3	6	Ж
31	2003-05-15	Волкова Лидия	175	52	Тяжелый форвард	3	7	Ж
32	2004-01-29	Карачаева Элина	170	60	Атакующий защитник	3	9	Ж
33	2004-06-01	Бушменкова Анна	174	68	Атакующий защитник	3	11	Ж
34	2004-04-19	Вигриянова Софья	178	63	Легкий форвард	3	12	Ж
35	2004-10-02	Сапогова Александра	175	65	Тяжелый форвард	3	13	Ж
36	2006-01-21	Черничкина Анастасия	183	69	Тяжелый форвард	3	14	Ж
37	2001-07-14	Чаплыгина Алина	176	67	Легкий Форвард	3	15	Ж
38	2000-11-19	Кириченкова Александра	179	50	Тяжелый форвард	3	19	Ж
39	2003-08-17	Юрина Анна	176	63	Атакующий защитник	3	20	Ж
40	2005-04-20	Людвиг Эмма	184	74	Разыгрывающий	3	21	Ж
41	2000-04-17	Македонский Денис	192	91	Разыгрывающий	4	0	М
42	2003-07-06	Качанюк Василий	180	75	Разыгрывающий	4	1	М
43	2005-12-28	Айриян Михаил	178	75	Центровой	4	4	М
44	2002-07-31	Кузнецов Александр	189	85	Легкий форвард	4	5	М
45	2002-02-08	Бочарников Антон	188	84	Разыгрывающий	4	6	М
46	2000-12-16	Комин Сергей	183	95	Атакующий защитник	4	7	М
47	1999-02-11	Каштанов Александр	180	77	Тяжелый форвард	4	11	М
48	2002-12-07	Беляев Арсений	191	84	Атакующий защитник	4	21	М
49	2002-03-29	Горгольцев Александр	199	107	Центровой	4	22	М
50	2005-01-20	Шульга Лука	183	99	Легкий форвард	4	54	М
51	2005-03-17	Гутор Андрей	185	71	Разыгрывающий	4	69	М
52	2004-06-14	Филонов Егор	187	74	Атакующий защитник	4	76	М
53	2003-03-16	Кирюков Даниил	184	80	Легкий форвард	4	99	М
54	2001-03-07	Дьяков Даниил	188	78	Атакующий защитник	5	1	М
55	1999-08-11	Лазаренко Виктор	185	82	Разыгрывающий	5	2	М
56	2001-09-15	Васильев Данила	194	85	Разыгрывающий	5	3	М
57	2004-02-01	Солдаткин Андрей	190	80	Атакующий защитник	5	5	М
58	2004-04-21	Баламутенко Григорий	196	81	Тяжелый форвард	5	7	М
59	2004-02-16	Дарьин Егор	185	80	Атакующий защитник	5	8	М
60	2005-02-11	Абросимов Семён	186	74	Атакующий защитник	5	9	М
61	2001-11-12	Чеботарев Артем	175	70	Атакующий защитник	5	10	М
62	2005-06-21	Горбачев Максим	200	75	Легкий форвард	5	11	М
63	2005-11-15	Евсеев Василий	187	80	Атакующий защитник	5	12	М
64	2002-06-09	Рыжков Матвей	209	90	Центровой	5	17	М
65	2000-05-17	Егоров Александр	192	68	Легкий форвард	5	22	М
66	2004-06-08	Осипов Ярослав	194	85	Разыгрывающий	5	23	М
67	2003-03-31	Акимов Данила	188	78	Атакующий защитник	6	1	М
68	2005-04-20	Блинников Владимир	185	82	Разыгрывающий	6	2	М
69	2003-09-01	Прохоренко Андрей	194	85	Атакующий защитник	6	3	М
70	2006-02-04	Спиридонов Матвей	190	80	Центровой	6	5	М
71	2000-03-25	Зубко Никита	196	81	Легкий форвард	6	7	М
72	2000-04-12	Сухобокий Николай	185	80	Центровой	6	8	М
73	2006-02-28	Цубаников Арсений	186	74	Центровой	6	9	М
74	2003-09-05	Барданов Василий	175	70	Атакующий защитник	6	10	М
75	2004-02-25	Сазонов Илья	200	75	Легкий форвард	6	11	М
76	2002-06-27	Мельгунов Сергей	187	80	Разыгрывающий	6	12	М
77	2005-01-15	Холодов Глеб	209	90	Атакующий защитник	6	17	М
78	2003-05-17	Латышев Александр	192	68	Разыгрывающий	6	22	М
79	2004-02-26	Моцный Роман	194	85	Разыгрывающий	6	23	М
\.


--
-- TOC entry 4854 (class 0 OID 16574)
-- Dependencies: 224
-- Data for Name: player_stats; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.player_stats (id_playerstats, pointscored, assists, steal, turnover, blocked_shot, foul, double, triple, free_throw, defensive_rebound, offensive_rebound, id_player, id_matches) FROM stdin;
1	10	5	2	8	3	4	7	2	1	3	1	1	2
2	15	6	3	12	4	5	9	3	2	4	2	2	2
3	12	7	2	10	2	6	6	1	3	5	1	3	2
4	8	4	1	6	1	2	5	2	0	2	1	2	2
5	20	10	5	15	6	8	12	7	3	7	2	1	2
6	17	9	4	14	5	7	11	5	2	6	2	3	2
7	13	6	3	11	3	5	8	3	1	4	1	1	2
8	16	8	4	13	4	6	10	4	2	5	2	2	2
9	11	5	2	9	2	4	7	1	1	3	1	3	2
10	14	7	3	12	3	6	9	3	2	4	2	2	2
11	9	4	1	7	1	3	6	2	0	2	1	2	2
12	18	9	4	15	5	7	13	6	3	7	2	1	2
13	16	8	4	13	4	6	10	4	2	5	2	3	2
14	10	5	2	8	2	4	6	1	1	3	1	1	2
15	13	6	3	11	3	5	8	3	1	4	1	2	2
16	22	11	6	18	7	9	15	8	4	9	2	3	2
18	12	6	2	10	2	4	7	2	1	3	1	2	2
19	19	10	5	16	6	8	14	7	3	8	2	3	2
20	14	7	3	11	3	5	9	3	1	4	1	2	2
21	8	4	1	6	1	2	5	1	0	2	1	3	2
22	21	11	6	17	7	9	14	8	4	9	2	1	2
23	13	6	3	11	3	5	8	3	1	4	1	2	2
24	18	9	4	15	5	7	12	6	3	7	2	2	2
25	16	8	4	13	4	6	10	4	2	5	2	3	2
26	10	5	2	8	2	4	7	2	1	3	1	2	2
17	15	7	3	12	3	5	9	3	2	4	1	1	2
31	10	2	2	2	2	0	2	2	2	2	0	8	9
159	7	1	0	0	2	0	2	1	0	2	0	24	76
160	5	2	0	1	1	1	1	1	0	0	0	16	76
154	0	0	0	1	0	0	0	0	0	0	0	15	76
161	9	0	0	0	0	1	3	1	0	0	0	14	76
158	8	0	0	1	0	0	2	1	1	0	0	13	76
162	6	0	0	0	1	0	2	0	2	0	0	12	76
163	6	0	0	0	0	0	0	2	0	0	0	11	76
164	7	0	0	0	0	0	2	1	0	1	2	9	76
165	10	1	1	0	1	1	3	1	1	1	1	8	76
166	15	0	0	0	0	0	2	3	2	0	0	6	76
155	3	0	1	0	0	0	0	1	0	0	0	5	76
167	6	1	1	0	1	1	0	2	0	1	1	4	76
157	7	0	0	0	0	0	1	1	2	0	0	3	76
156	1	0	0	0	0	0	0	0	1	0	0	2	76
168	4	1	0	0	1	1	0	1	1	2	0	1	76
170	3	0	0	0	0	0	0	1	0	0	0	45	77
171	5	0	0	0	0	0	1	1	0	0	0	44	77
172	2	0	0	0	0	0	1	0	0	0	0	43	77
169	0	1	0	0	0	0	0	0	0	0	0	58	77
173	0	0	0	0	1	0	0	0	0	0	0	56	77
174	6	0	0	0	0	0	3	0	0	0	0	14	78
175	0	0	0	1	1	0	0	0	0	0	0	13	78
176	6	0	0	0	0	0	0	2	0	0	0	4	78
177	2	0	0	0	0	0	1	0	0	0	0	3	78
178	0	0	1	0	0	0	0	0	0	0	0	2	78
179	2	0	0	0	0	0	1	0	0	0	0	45	79
180	6	0	0	0	0	0	0	2	0	0	0	44	79
181	0	0	0	0	1	0	0	0	0	0	0	43	79
182	3	0	1	0	0	0	0	1	0	0	0	57	79
183	2	0	0	0	0	0	1	0	0	0	0	56	79
184	3	0	0	1	0	0	0	1	0	0	0	49	79
\.


--
-- TOC entry 4851 (class 0 OID 16561)
-- Dependencies: 220
-- Data for Name: team; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.team (id_team, city, team_name, gender_team) FROM stdin;
2	Москва	РЭУ	Ж
5	Москва	РЭУ	М 
7	Москва	РУС-ГЦОЛИФК	Ж 
8	Москва	РУС-ГЦОЛИФК	М
6	Москва	РГАУ-МСХА	М 
3	Москва	РГАУ-МСХА	Ж
4	Москва	РГУ им. А.Н.Косыгина	М 
1	Москва	РГУ им. А.Н.Косыгина	Ж 
9	МОСКВА	РГУ1	Ж
10	МОСКВА	РГУ2	Ж
\.


--
-- TOC entry 4861 (class 0 OID 0)
-- Dependencies: 218
-- Name: m_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.m_seq', 79, true);


--
-- TOC entry 4862 (class 0 OID 0)
-- Dependencies: 222
-- Name: pl_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.pl_seq', 47, true);


--
-- TOC entry 4863 (class 0 OID 0)
-- Dependencies: 226
-- Name: ps_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.ps_seq', 184, true);


--
-- TOC entry 4676 (class 2606 OID 16589)
-- Name: eventt eventt_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eventt
    ADD CONSTRAINT eventt_pkey PRIMARY KEY (id_event);


--
-- TOC entry 4679 (class 2606 OID 16591)
-- Name: level_event level_event_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.level_event
    ADD CONSTRAINT level_event_pkey PRIMARY KEY (id_competition_level);


--
-- TOC entry 4682 (class 2606 OID 16593)
-- Name: matches matches_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.matches
    ADD CONSTRAINT matches_pkey PRIMARY KEY (id_matches);


--
-- TOC entry 4688 (class 2606 OID 16595)
-- Name: player player_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.player
    ADD CONSTRAINT player_pkey PRIMARY KEY (id_player);


--
-- TOC entry 4691 (class 2606 OID 16597)
-- Name: player_stats player_stats_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.player_stats
    ADD CONSTRAINT player_stats_pkey PRIMARY KEY (id_playerstats);


--
-- TOC entry 4685 (class 2606 OID 16599)
-- Name: team team_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.team
    ADD CONSTRAINT team_pkey PRIMARY KEY (id_team);


--
-- TOC entry 4689 (class 1259 OID 16600)
-- Name: xpkИгрок; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX "xpkИгрок" ON public.player USING btree (id_player);


--
-- TOC entry 4686 (class 1259 OID 16601)
-- Name: xpkКоманда; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX "xpkКоманда" ON public.team USING btree (id_team);


--
-- TOC entry 4683 (class 1259 OID 16602)
-- Name: xpkМатч; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX "xpkМатч" ON public.matches USING btree (id_matches);


--
-- TOC entry 4677 (class 1259 OID 16603)
-- Name: xpkСоревнование; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX "xpkСоревнование" ON public.eventt USING btree (id_event);


--
-- TOC entry 4692 (class 1259 OID 16604)
-- Name: xpkСтатистика_игроков; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX "xpkСтатистика_игроков" ON public.player_stats USING btree (id_playerstats);


--
-- TOC entry 4680 (class 1259 OID 16605)
-- Name: xpkУровень_соревнования; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX "xpkУровень_соревнования" ON public.level_event USING btree (id_competition_level);


--
-- TOC entry 4693 (class 2606 OID 16606)
-- Name: eventt eventt_id_competition_level_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.eventt
    ADD CONSTRAINT eventt_id_competition_level_fkey FOREIGN KEY (id_competition_level) REFERENCES public.level_event(id_competition_level);


--
-- TOC entry 4694 (class 2606 OID 16611)
-- Name: matches matches_id_event_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.matches
    ADD CONSTRAINT matches_id_event_fkey FOREIGN KEY (id_event) REFERENCES public.eventt(id_event);


--
-- TOC entry 4695 (class 2606 OID 16616)
-- Name: matches matches_id_team1_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.matches
    ADD CONSTRAINT matches_id_team1_fkey FOREIGN KEY (id_team1) REFERENCES public.team(id_team);


--
-- TOC entry 4696 (class 2606 OID 16621)
-- Name: matches matches_id_team2_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.matches
    ADD CONSTRAINT matches_id_team2_fkey FOREIGN KEY (id_team2) REFERENCES public.team(id_team);


--
-- TOC entry 4697 (class 2606 OID 16626)
-- Name: player player_id_team_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.player
    ADD CONSTRAINT player_id_team_fkey FOREIGN KEY (id_team) REFERENCES public.team(id_team);


--
-- TOC entry 4698 (class 2606 OID 16631)
-- Name: player_stats player_stats_id_matches_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.player_stats
    ADD CONSTRAINT player_stats_id_matches_fkey FOREIGN KEY (id_matches) REFERENCES public.matches(id_matches);


--
-- TOC entry 4699 (class 2606 OID 16636)
-- Name: player_stats player_stats_id_player_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.player_stats
    ADD CONSTRAINT player_stats_id_player_fkey FOREIGN KEY (id_player) REFERENCES public.player(id_player);


-- Completed on 2024-12-10 13:47:07

--
-- PostgreSQL database dump complete
--

