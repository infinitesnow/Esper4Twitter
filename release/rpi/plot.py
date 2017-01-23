#!/usr/bin/python3.5
import matplotlib.pyplot as plt
import matplotlib.dates as mdate
import numpy as np
import os
import pytz
mdate.rcParams['timezone'] = 'Europe/Rome'
#mdate.rcParams['timezone'] = 'US/Pacific'
data=np.genfromtxt('/home/infinitesnow/Documenti/data/total.log',delimiter=',',names=['epoch','count','ratio'])
epoch=data['epoch']
count=data['count']
ratio=data['ratio']
secs=mdate.epoch2num(epoch)
fig, ax1 = plt.subplots()
plt.xticks(rotation='50')
ax1.plot_date(secs,count,'k')
ax2=ax1.twinx()
ax2.plot_date(secs,ratio,'r')
ax1.xaxis.set_major_locator(mdate.HourLocator(interval=4))
ax1.xaxis.set_major_formatter(mdate.DateFormatter('%b %d %k:%M'))
plt.show()
