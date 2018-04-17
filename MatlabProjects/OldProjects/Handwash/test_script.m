format long g
e = data(2).events;
offset = e(1,1) - e(1,2);
e(:,2) = e(:,2) +offset;
diff = e(:, 1) - e(:,2)

max(diff)
min(diff)