function plot_data_sphere_multi_chunks(data, ttl, sphere_flag, ico, ico_number, cell_no_flag, scatter_flag)    
    figure;
    if sphere_flag == true
        t = ico(ico_number).triangles;
        e = ico(ico_number).edges;
        v = ico(ico_number).vertices;
        vp = ico(ico_number).voronoi_points;
        vr = ico(ico_number).voronoi_regions;
        
        r = 0.97;
        [x,y,z] = sphere(20);
        mesh(x*r, y*r, z*r, 'FaceColor',[0.95 0.95 0.95], 'LineStyle','none'); 
        hold on
        vr_count = length(vr);
        for i = 1:vr_count         
            a = vr{i};
            a = [a; a(1,:)];
            plot3(a(:,1), a(:, 2), a(:, 3));    
        end
 
        if cell_no_flag
            a = (1:length(v))'; b = num2str(a); c = cellstr(b);
            dx = 1.10; % displacement so the text does not overlay the data points
            text(v(:,1)*dx, v(:,2)*dx, v(:,3)*dx, c);
            scatter3(v(:,1), v(:, 2), v(:, 3), 'b.');
        end
    end
    
    
    d = data{1};
    size(d)
    if scatter_flag
        scatter3(d(:,1), d(:, 2), d(:, 3), 'x');
    else
        plot3(d(:,1), d(:, 2), d(:, 3));        
    end
    
    title(ttl);
    grid on
    hold on
    count = length(data);
    
    for i=2:count
        d = data{i};
        if scatter_flag
            scatter3(d(:,1), d(:, 2), d(:, 3), 'x');
        else
            plot3(d(:,1), d(:, 2), d(:, 3));
        end
            
        %waitforbuttonpress
    end
    
    xlabel('X');
    ylabel('Y');
    zlabel('Z');
    
end