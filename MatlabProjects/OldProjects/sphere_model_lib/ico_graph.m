factor = 2;
t = ico(factor).triangles;
e = ico(factor).edges;
v = ico(factor).vertices;
vp = ico(factor).voronoi_points;
vr = ico(factor).voronoi_regions;

figure;
r = 0.92;
[x,y,z] = sphere(20);
mesh(x*r, y*r, z*r, 'FaceColor',[0.95 0.95 0.95], 'LineStyle','none'); 
hold on;

% e_count = length(e);
% e = [v(e(:,1), :), v(e(:,2), :)];
% for i = 1:e_count         
%     plot3([e(i,1);e(i,4)], [e(i,2);e(i,5)], [e(i,3);e(i,6)]);
% end

vr_count = length(vr);
for i = 1:vr_count         
    a = vr{i};
    a = [a; a(1,:)];
    plot3(a(:,1), a(:, 2), a(:, 3));    
end

a = [1:length(v)]'; b = num2str(a); c = cellstr(b);
dx = 0.02; dy = 0.02; dz = 0.02; % displacement so the text does not overlay the data points
text(v(:,1)+dx, v(:,2)+dy, v(:,3)+dz, c);

scatter3(v(:,1), v(:, 2), v(:, 3), 'b.');
%scatter3(vp(:,1), vp(:, 2), vp(:, 3), 'bx');
