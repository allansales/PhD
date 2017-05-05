DROP LIBRARY stocks_lib CASCADE;

\set libfile '\''`pwd`'/stocks_udtf_r_lib.R\''
CREATE LIBRARY stocks_lib AS :libfile LANGUAGE 'R';

CREATE OR REPLACE TRANSFORM FUNCTION DetectSolavanco
AS LANGUAGE 'R' NAME 'DetectSolavancoFactory' LIBRARY stocks_lib;
