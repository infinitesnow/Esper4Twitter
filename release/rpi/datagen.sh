echo "Cleaning..."
rm -r logs
> total.log
echo "Copying files..."
scp -r lele@infinitesnow.duckdns.org:~/rpi/rpi.new/logs .
cd logs
echo "Gunzipping..."
gunzip */esper*.gz
for folder in */; do
	echo "Changing directory into $folder"
	cd $folder
	folder=${folder%"/"}
	echo "Parsing files $( ls | grep esper*)"
	cat esper*.log | while read p; do 
		date=$(echo $p | grep -oP '\[\d+:\d+:\d+\]' | perl -pe "s/[\[\]]//g")
		date=$(($(date -d "$folder $date" +%s)+3600))
		echo "$date,$(echo $p | perl -pe 's/\[.*?\]\s//g')"
	done >> ../../total.log
	echo "Done"
	cd ..
done
cd ..
./plot.py
