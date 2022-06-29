import sqlite3
import os
import openpyxl as px
import numpy as np
import pandas as pd
from datetime import datetime



rpath = "../scheduler/backend/demo.db"


conn = sqlite3.connect(rpath)



"""with conn:
    cur = conn.cursor()
    cur.execute(''' DROP TABLE stats''')
    cur.execute('''CREATE TABLE stats
         ( id INT PRIMARY KEY     NOT NULL,
         Coverage           REAL    NOT NULL,
         Balance           REAL    NOT NULL,
         Fairness           REAL    NOT NULL);''')

    cur.execute('''INSERT INTO stats(id, Coverage, Balance, Fairness) VALUES(?,?,?,?)''', (1, 0.0, 0.0, 0.0))
    #cur.execute('''INSERT INTO shift_type_parameters(shift_type, shift_workload, shift_coverage, max_buffer) VALUES(?,?,?,?)''', ("SANW", 0.0, 0.0, 0.0))
    conn.commit()"""

with conn:
    cur = conn.cursor()
    cur.execute(''' DROP TABLE schedule''')
    cur.execute('''CREATE TABLE stats
         ( id INT PRIMARY KEY     NOT NULL,
         Coverage           REAL    NOT NULL,
         Balance           REAL    NOT NULL,
         Fairness           REAL    NOT NULL);''')

    cur.execute('''INSERT INTO stats(id, Coverage, Balance, Fairness) VALUES(?,?,?,?)''', (1, 0.0, 0.0, 0.0))
    #cur.execute('''INSERT INTO shift_type_parameters(shift_type, shift_workload, shift_coverage, max_buffer) VALUES(?,?,?,?)''', ("SANW", 0.0, 0.0, 0.0))
    conn.commit()