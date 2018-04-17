sub_count = length(data);
edges = -12:3:0
edges_count = length(edges);

xe = [];
xd = [];
for sub = 1:34    
        accel = data(sub).accel;
        a = data(sub).annots;
        a = get_annot_indices(accel, a);
        ae = a(a(:, 2)<400, 1);
        xe = [xe; accel(ae, 2)];
        ad = a(a(:, 2)>=400 & a(:, 2)<1000, 1);
        xd = [xd; accel(ad, 2)];
end

[NE, ~] = histcounts(xe, edges);
[ND, ~] = histcounts(xd, edges);    
res = [NE', ND'];    
res = [edges(1:end-1)', edges(2:end)', res]

return
figure;
plot(res(:,2), NE);
grid on
hold on
plot(res(:,2), ND);
legend('Eat','Drink')
