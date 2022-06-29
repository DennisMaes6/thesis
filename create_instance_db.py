import sqlite3
import os
import openpyxl as px
import numpy as np
import pandas as pd
from datetime import datetime

from pyrsistent import v


rpath = "../scheduler/backend/demo.db"



verlof = {
    '41': [1,8,12, 19],
    '42': [3, 10, 14, 21],
    '43': [4, 6, 22, 25],
    '44': [6, 9, 13, 18],
    '45': [5, 7, 11, 16],
    '46': [3, 10, 16, 24],
    '47': [6, 9, 23, 25],
    '48': [8, 18, 22, 24  ],
    '49': [2, 18],

    '50': [2, 10, 12],
    '51': [7, 17, 23],

    '52': [2, 16, 24],

    '53': [1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
    '54': [10, 14, 22],
    '55': [2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13],
    '56': [1, 8, 12, 14],
    '57': [1, 3, 6],

    '58': [20, 22, 26],
    '59': [2, 9, 13, 15],
    '60': [10, 20],
    '61': [6, 9, 20, 22],
    '62': [8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19],
    '63': [3, 5, 23, 25],
    '64': [11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25],
    '65': [11, 15, 23],
    '66': [1, 11, 16, 19],
    '67': [13, 19],
    '68': [6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19],
    '69': [12, 16, 24],
    '70': [6, 10, 17],
    '71': [ 19, 20, 21, 22, 23, 24, 25],

    '72': [13, 17, 26],
    '73': [4, 5, 6, 7, 8],
    '74': [7, 14, 21],
    '75': [2, 3, 4, 5, 6, 7, 8, 9],
    '76': [15, 23, 26]
}

print(verlof.keys())
def week_nb_to_string(wk_nb):
    result = ''
    for i in range( (wk_nb-1) * 7 + 1, (wk_nb-1) * 7 + 8):
        result += str(i)
        result += ','

    return result


def multiple_wks_string(week_list):
    result = ''
    for wk in week_list:
        result += week_nb_to_string(wk)
    
    return result

conn = sqlite3.connect(rpath)

"""with conn: 
    cur = conn.cursor()

    ids = []
    for id in cur.execute('''SELECT id FROM assistant'''):
        ids.append(id[0])
    
    print(ids)
    for id in ids:
        if str(id) in verlof.keys():
            print(multiple_wks_string(verlof[str(id)]))
            cur.execute('''UPDATE assistant SET free_days=? WHERE id=?''',( multiple_wks_string(verlof[str(id)]), id))

    
            conn.commit()"""


with conn:
    cur = conn.cursor()
    cur.execute('''UPDATE shift_type_parameters SET shift_type=? WHERE shift_type=?''', ("SANW", "JANW"))
    #cur.execute('''INSERT INTO shift_type_parameters(shift_type, shift_workload, shift_coverage, max_buffer) VALUES(?,?,?,?)''', ("SANW", 0.0, 0.0, 0.0))
    conn.commit()