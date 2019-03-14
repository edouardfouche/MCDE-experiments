/*
 * Copyright (C) 2018 Edouard Fouché
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

//import com.edouardfouche.generators_deprecated._
import io.github.edouardfouche.generators._

val n = 1000
for {noise <- Array(0.0, 0.1, 0.2, 0.5, 1.0)} {
  for {dim <- Array(2, 3, 5)} {
    Independent(dim, noise, "gaussian", 0).save(n)
    Cross(dim, noise, "gaussian", 0).save(n)
    DoubleLinear(dim, noise, "gaussian", 0)(Some(0.25)).save(n)
    Hourglass(dim, noise, "gaussian", 0).save(n)
    Hypercube(dim, noise, "gaussian", 0).save(n)
    HypercubeGraph(dim, noise, "gaussian", 0).save(n)
    HyperSphere(dim, noise, "gaussian", 0).save(n)
    Linear(dim, noise, "gaussian", 0).save(n)
    Parabola(dim, noise, "gaussian", 0)(Some(1)).save(n)
    Sine(dim, noise, "gaussian", 0)(Some(1)).save(n)
    Sine(dim, noise, "gaussian", 0)(Some(5)).save(n)
    Star(dim, noise, "gaussian", 0).save(n)
    Zinv(dim, noise, "gaussian", 0).save(n)
  }
}

Independent(100, 0.0, "gaussian", 0).save(n)